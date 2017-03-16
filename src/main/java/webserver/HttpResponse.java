package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
	private DataOutputStream dos;
	private Map<String, String> sendHeaderList = new HashMap<>();
	
	public HttpResponse(OutputStream out) {
		// TODO Auto-generated constructor stub
		dos = new DataOutputStream(out);
	}
	
	public void addheader(String header, String value) {
		// TODO Auto-generated method stub
		try{
			sendHeaderList.put(header, value);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void forward(String url) throws IOException {
		// TODO Auto-generated method stub
		byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
		if(url.endsWith(".css")) {
			sendHeaderList.put("Content-Type", "text/css");
		} else if(url.endsWith(".js")) {
			sendHeaderList.put("Content-Type", "application/javascript");
		} else {
			sendHeaderList.put("Content-Type", "text/html;charset=utf-8");
		}
		sendHeaderList.put("Content-Length", body.length + "");
        response200Header(body.length);
        responseBody(body);
	}

	public void forwardBody(String body) {
		byte[] contents = body.getBytes();
		sendHeaderList.put("Content-Type", "text/html;charset=utf-8");
		sendHeaderList.put("Content-Length", contents.length + "");
		response200Header(contents.length);
        responseBody(contents);
	}
	
	public void sendRedirect(String location) throws IOException {
	    try {
	        dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
	        processHeader();
	        dos.writeBytes("Location: " + location + "\r\n");
	        dos.writeBytes("\r\n");
	    } catch (IOException e) {
	        log.error(e.getMessage());
	    }
    }
	
	private void response200Header(int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            processHeader();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
	
	private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
	
	private void processHeader() throws IOException {
		Set<String> keys = sendHeaderList.keySet();
		for (String key : keys) {
			dos.writeBytes(key + ": " + sendHeaderList.get(key) + "\r\n");
			log.debug("response header : {}", key + ": " + sendHeaderList.get(key));
		}
	}
}
