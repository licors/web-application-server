package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;

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
        	HttpRequest request = new HttpRequest(in);
        	HttpResponse response = new HttpResponse(out);
        	
        	String url = request.getPath();

    		// 1. url 검사(회원가입)
    		if(url.equals("/user/create")) {
	    		User user = new User(request.getParameter("userId"), request.getParameter("password"), 
	    								request.getParameter("name"), request.getParameter("email"));
	    		log.debug("User class data {}",user);
	    		DataBase.addUser(user);
	    		
	    		//응답 데이터 만들어서 응답함
	    		response.sendRedirect("/index.html");
    		}
    		
    		else if(url.equals("/user/login")) {
	    		User user = DataBase.findUserById(request.getParameter("userId"));
	    		log.debug("User class data {}",user);
	    		
	    		if(user == null) {
	    			response.forward("/user/login_failed.html");
	    			return;
	    		}
	    		
	    		if(user.getPassword().equals(request.getParameter("password"))) {
	    			//로그인 성공 시 index.html 로 이동
	    			response.addheader("Set-Cookie", "logined=true");
	    			response.sendRedirect("/index.html");
	    		}
	    		else {
	    			//로그인 실패
	    			response.forward("/user/login_failed.html");
	    		}
    		}
    		
    		else if(url.equals("/user/list")) {
    			if(isLogin(request)) {
    				response.forward("/user/login.html");
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
		        response.forwardBody(userListOutput.toString());
    		}
    		
    		else {
    			//응답 데이터 만들어서 응답함
    			response.forward(url);
    		}
    		
    		
    		
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private boolean isLogin(HttpRequest request) {
    	String value = request.getHeader("Cookie");
    	if(value == null) {
    		return false;
    	}
    	return Boolean.parseBoolean(value);
    }
}
