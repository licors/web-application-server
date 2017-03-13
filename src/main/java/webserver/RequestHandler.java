package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); 
        		OutputStream out = connection.getOutputStream()) {
        	
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
    		BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
    		String line = br.readLine();
    		log.debug("request line : {}",line);
    		
    		if(line == null) {
    			return;
    		}
    		
    		String[] tokens = line.split(" ");
    		String url = tokens[1];;
    		String queryString = "";
    		boolean logined = false;
    		int contentLength = -1;
    		
    		while(!line.equals("")) {
    			line = br.readLine();
    			log.debug("header : {}",line);
    			if(line.contains("Content-Length")) {
    				contentLength = getContentLength(line);
    			}
    			if(line.contains("Cookie")) {
    				logined = isLogin(line);
    			}
    		}

    		// 1. url 검사(회원가입)
    		if(url.startsWith("/user/create")) {
    			
    			int index = url.indexOf("?");
    			// 2-1. get방식인 경우
	    		if(index != -1) {
		    		queryString = url.substring(index+1);
	    		}
	    		
	    		// 2-2. post 방식인 경우
	    		if(contentLength != -1) {
	    			queryString = IOUtils.readData(br, contentLength);
	    		}
	    		
	    		Map<String, String> userData = HttpRequestUtils.parseQueryString(queryString);
	    		User user = new User(userData.get("userId"), userData.get("password"), 
	    								userData.get("name"), userData.get("email"));
	    		log.debug("User class data {}",user);
	    		DataBase.addUser(user);
	    		
	    		//응답 데이터 만들어서 응답함
                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos, "http://localhost:8080/index.html");
    		}
    		
    		else if(url.equals("/user/login")) {
    			queryString = IOUtils.readData(br, contentLength);
	    		Map<String, String> params = HttpRequestUtils.parseQueryString(queryString);
	    		User user = DataBase.findUserById(params.get("userId"));
	    		log.debug("User class data {}",user);
	    		
	    		if(user == null) {
	    			responseResource(out,"/user/login_failed.html");
	    			return;
	    		}
	    		
	    		if(user.getPassword().equals(params.get("password"))) {
	    			//로그인 성공 시 index.html 로 이동
	                DataOutputStream dos = new DataOutputStream(out);
	                response302LoginSuccess(dos);
	    		}
	    		else {
	    			//로그인 실패
	    			responseResource(out,"/user/login_failed.html");
	    		}
    		}
    		
    		else if(url.equals("/user/list")) {
    			if(!logined) {
    				responseResource(out,"/user/login.html");
    				return;
    			}
				Collection<User> userList = DataBase.findAll();
				StringBuilder userListOutput = new StringBuilder();
				userListOutput.append("<table align='center' border='1'>");
				userListOutput.append("<tr><td>ID</><td>Name</><td>Email</></tr>");
				for (User user : userList) {
					userListOutput.append("<tr>");
					userListOutput.append("<td>" + user.getUserId() + "</>");
					userListOutput.append("<td>" + user.getName() + "</>");
					userListOutput.append("<td>" + user.getEmail() + "</>");
					userListOutput.append("</tr>");
				}
				userListOutput.append("</table>");

				DataOutputStream dos = new DataOutputStream(out);
		        byte[] body = userListOutput.toString().getBytes();
		        response200Header(dos, body.length);
    		    responseBody(dos, body);
    		}
    		
    		else if(url.endsWith(".css")) {
    			DataOutputStream dos = new DataOutputStream(out);
    	        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
    	        responseCSS200Header(dos, body.length);
    	        responseBody(dos, body);
    		}
    		
    		else {
    			//응답 데이터 만들어서 응답함
    			responseResource(out,url);
    		}
    		
    		
    		
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private boolean isLogin(String line) {
    	String[] headerData = line.split(":");
    	Map<String, String> parameters = HttpRequestUtils.parseCookies(headerData[1].trim());
    	String value = parameters.get("logined");
    	if(value == null) {
    		return false;
    	}
    	return Boolean.parseBoolean(value);
    }

    private int getContentLength(String line) {
    	String[] headerData = line.split(":");
    	return Integer.parseInt(headerData[1].trim());
    }
    
    private void responseResource(OutputStream out, String url) throws IOException {
    	DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }
    
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void responseCSS200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css \r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
	private void response302Header(DataOutputStream dos, String location) {
	    try {
	        dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
	        dos.writeBytes("Location: " + location + "\r\n");
	        dos.writeBytes("\r\n");
	    } catch (IOException e) {
	        log.error(e.getMessage());
	    }
    }
	
	private void response302LoginSuccess(DataOutputStream dos) {
	    try {
	        dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
	        dos.writeBytes("Set-Cookie: logined=true \r\n");
	        dos.writeBytes("Location: /index.html \r\n");
	        dos.writeBytes("\r\n");
	    } catch (IOException e) {
	        log.error(e.getMessage());
	    }
    }
	
    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
