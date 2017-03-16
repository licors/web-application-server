package webserver.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

public class LoginController extends AbstractController{
	private static final Logger log = LoggerFactory.getLogger(LoginController.class);
	
	@Override
	public void doPost(HttpRequest request, HttpResponse response) {
		// TODO Auto-generated method stub
		User user = DataBase.findUserById(request.getParameter("userId"));
		log.debug("User class data {}",user);
		try {
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
