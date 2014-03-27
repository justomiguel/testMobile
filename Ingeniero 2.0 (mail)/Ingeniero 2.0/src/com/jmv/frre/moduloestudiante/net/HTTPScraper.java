package com.jmv.frre.moduloestudiante.net;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cleo on 12/1/13.
 */
public class HTTPScraper {

	public static final String MAINTENANCE_MODE_ON = "MAINTENANCE_MODE_ON";
	public static HttpClient client = new DefaultHttpClient();
   
	private static String cookies;
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36";

    static {
    	// make sure cookies is turn on
    	CookieHandler.setDefault(new CookieManager());
    }
    
	public static String getCookies() {
		return cookies;
	}
	public static void setCookies(String cookies) {
		HTTPScraper.cookies = cookies;
	}
	
	
	public String fecthHtmlGet(String url){
        HttpResponse response;
        String responseString = null;
        
        HttpGet request = new HttpGet(url);
        
        request.setHeader("User-Agent", USER_AGENT);
    	request.setHeader("Accept",
    		"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    	request.setHeader("Accept-Language", "en-US,en;q=0.5");
     
    	
        try {
            response = client.execute(request);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                setCookies(response.getFirstHeader("Set-Cookie") == null ? "" : 
                    response.getFirstHeader("Set-Cookie").toString());
                return responseBody;
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return "";
    }
    public String fetchPageHtmlPost(String url, List<NameValuePair> nameValuePair)  {

        HttpPost post = new HttpPost(url);
        
     // add header
    	post.setHeader("Host", "sysacadweb.frre.utn.edu.ar");
    	post.setHeader("User-Agent", USER_AGENT);
    	post.setHeader("Accept", 
                 "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    	post.setHeader("Accept-Language", "en-US,en;q=0.5");
    	post.setHeader("Cookie", getCookies());
    	post.setHeader("Connection", "keep-alive");
    	
    	if (url.indexOf("http://sysacadweb.frre.utn.edu.ar/menuAlumno.asp")!=-1){
    		post.setHeader("Referer", "http://sysacadweb.frre.utn.edu.ar/loginAlumno.asp");
    	}
    	
    	post.setHeader("Content-Type", "application/x-www-form-urlencoded");
     
        try {
            if (nameValuePair==null){
                nameValuePair = new ArrayList<NameValuePair>();
            }
            post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if(entity != null && response.getStatusLine().getStatusCode()==200)
            {
                String responseBody = EntityUtils.toString(entity);
                return responseBody;
            } else {
            	return MAINTENANCE_MODE_ON;
            }
            // System.out.println(resp);
        } catch (IOException e) {
        	return MAINTENANCE_MODE_ON;
        }
    }
}
