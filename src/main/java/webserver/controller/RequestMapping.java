package webserver.controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
	static Map<String, Controller> controllers = new HashMap<>();
	
	static{
		controllers.put("/user/create", new CreateUserController());
		controllers.put("/user/list", new ListUserController());
		controllers.put("/user/login", new LoginController());
	}
	
	public static Controller getController(String requestUrl) {
		return controllers.get(requestUrl);
	}
}
