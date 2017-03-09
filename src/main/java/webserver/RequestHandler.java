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
    		int contentLength = -1;
    		
    		while(!line.equals("")) {
    			line = br.readLine();
    			log.debug("header : {}",line);
    			if(line.contains("Content-Length")) {
    				contentLength = getHeaderValue(line);
    			}
    		}

    		// 1. url 검사
    		if(url.startsWith("/user/create")) {
    			
    			int index = url.indexOf("?");
    			// 2-1. get방식인 경우
	    		if(index != -1) {
		    		queryString = url.substring(index+1);
		    		//DataBase.addUser(user);
	    		}
	    		
	    		// 2-2. post 방식인 경우
	    		if(contentLength != -1) {
	    			queryString = IOUtils.readData(br, contentLength);
	    		}
	    		
	    		Map<String, String> userData = HttpRequestUtils.parseQueryString(queryString);
	    		User user = new User(userData.get("userId"), userData.get("password"), 
	    								userData.get("name"), userData.get("email"));
	    		log.debug("User class data {}",user);
    		}
    		else {
    			//응답 데이터 만들어서 응답함
                DataOutputStream dos = new DataOutputStream(out);
                //byte[] body = "Hello WorldTest".getBytes();    //서버 동작 테스트용
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
    		}
    		
    		
    		
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private int getHeaderValue(String line) {
    	String[] headerData = line.split(":");
    	return Integer.parseInt(headerData[1].trim());
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
