package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.controller.Controller;
import webserver.controller.CreateUserController;
import webserver.controller.ListUserController;
import webserver.controller.LoginController;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

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
        	Map<String, Controller> controllers = new HashMap<>();
        	controllers.put("/user/create", new CreateUserController());
        	controllers.put("/user/list", new ListUserController());
        	controllers.put("/user/login", new LoginController());
        	        	
        	Controller controller = controllers.get(url);
        	if(controller == null) {
        		response.forward(url);
        		return;
        	}
        	controller.service(request, response);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
