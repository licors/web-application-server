package webserver.controller;

import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

public abstract class AbstractController implements Controller{

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		// TODO Auto-generated method stub
		String method = request.getMethod();
		if(method == null) {
			return;
		} else if(method.equals("POST")) {
			doPost(request, response);
		} else if(method.equals("GET")) {
			doGet(request, response);
		}
	}
	
	public void doPost(HttpRequest request, HttpResponse response){}
	public void doGet(HttpRequest request, HttpResponse response){}
	
}
