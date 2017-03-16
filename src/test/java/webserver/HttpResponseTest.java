package webserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.Test;

import webserver.http.HttpResponse;

public class HttpResponseTest {
	private final String testDirectoryPath = "./src/test/resource/";
	
	@Test
	public void response_forward() throws Exception {
		OutputStream out = new FileOutputStream(new File(testDirectoryPath+"HttpForward.txt"));
		HttpResponse response = new HttpResponse(out);	
		response.forward("/index.html");
	}
	
	@Test
	public void response_response() throws Exception {
		OutputStream out = new FileOutputStream(new File(testDirectoryPath+"HttpRedirect.txt"));
		HttpResponse response = new HttpResponse(out);
		response.sendRedirect("/index.html");
	}
	
	@Test
	public void response_cookie() throws Exception {
		OutputStream out = new FileOutputStream(new File(testDirectoryPath+"HttpCookie.txt"));
		HttpResponse response = new HttpResponse(out);
		response.addheader("Set-Cookie","logined=true");
		response.sendRedirect("/index.html");
	}
}
