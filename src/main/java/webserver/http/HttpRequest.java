package webserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import util.HttpRequestUtils;
import util.IOUtils;

public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
	private InputStream in = null;
	private String method;
	private String path;
	private Map<String, String> headerList;
	private Map<String, String> parameter;

	public HttpRequest(InputStream in) {
		// TODO Auto-generated constructor stub
		this.in = in;
		headerList = Maps.newHashMap();
		parameter = Maps.newHashMap();
		parser();
	}

	private void parser() {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = br.readLine();
			log.debug("request line : {}", line);

			if (line == null) {
				return;
			}

			String[] tokens = line.split(" ");
			method = tokens[0];
			path = tokens[1];
			String queryString = "";

			while (!line.equals("")) {
				line = br.readLine();
				log.debug("header : {}", line);
				if(line.contains(":")) {
					parseHeader(line);
				}
			}
			
			int index = path.indexOf("?");
			//queryString extract
    		if(index != -1) {
	    		queryString = path.substring(index+1);
	    		path = path.substring(0,index);
    		}
    		else if(headerList.containsKey("Content-Length")) {
    			int contentLength = Integer.parseInt(headerList.get("Content-Length"));
    			queryString = IOUtils.readData(br, contentLength);
	    	}
    		parameter = HttpRequestUtils.parseQueryString(queryString);
    		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseHeader(String line) {
	    	String[] headerData = line.split(":", 2);
	    	headerList.put(headerData[0].trim(), headerData[1].trim());
    }
	
	public String getMethod() {
		// TODO Auto-generated method stub
		return method;
	}

	public String getPath() {
		// TODO Auto-generated method stub
		return path;
	}

	public String getHeader(String string) {
		// TODO Auto-generated method stub
		return headerList.get(string);
	}

	public String getParameter(String string) {
		// TODO Auto-generated method stub
		return parameter.get(string);
	}

}
