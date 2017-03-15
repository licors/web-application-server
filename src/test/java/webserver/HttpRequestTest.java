package webserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.FileInputStream;

import java.io.InputStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestTest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequestTest.class);
	private final String testDirectoryPath = "./src/test/resource/";
	
	@Test
	public void request_Get() throws Exception{
		InputStream in = new FileInputStream(new File(testDirectoryPath+"HTTP_Get.txt"));
		HttpRequest request = new HttpRequest(in);
		
		assertEquals("GET",request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("aaa", request.getParameter("userId"));
	}
	
	@Test
	public void request_Post() throws Exception{
		InputStream in = new FileInputStream(new File(testDirectoryPath+"HTTP_Post.txt"));
		HttpRequest request = new HttpRequest(in);
		
		assertEquals("POST",request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("bbb", request.getParameter("userId"));
	}
}
