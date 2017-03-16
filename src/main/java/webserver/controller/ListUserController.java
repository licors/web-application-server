package webserver.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

public class ListUserController extends AbstractController{
	private static final Logger log = LoggerFactory.getLogger(ListUserController.class);
	
	@Override
	public void doGet(HttpRequest request, HttpResponse response) {
		// TODO Auto-generated method stub
		if(!isLogin(request.getHeader("Cookie"))) {
			try {
				response.forward("/user/login.html");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	public boolean isLogin(String cookies) {
		Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies.trim());
    	String value = parameters.get("logined");
    	if(value == null) {
    		return false;
    	}
    	return Boolean.parseBoolean(value);
	}
}
