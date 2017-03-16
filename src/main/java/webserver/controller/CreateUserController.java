package webserver.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

public class CreateUserController extends AbstractController {
	private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
	
	@Override
	public void doPost(HttpRequest request, HttpResponse response) {
		// TODO Auto-generated method stub
		User user = new User(request.getParameter("userId"), request.getParameter("password"),
				request.getParameter("name"), request.getParameter("email"));
		log.debug("User class data {}", user);
		DataBase.addUser(user);

		// 응답 데이터 만들어서 응답함
		try {
			response.sendRedirect("/index.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
