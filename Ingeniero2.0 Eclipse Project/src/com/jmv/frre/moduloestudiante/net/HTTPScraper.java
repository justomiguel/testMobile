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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cleo on 12/1/13.
 */
public class HTTPScraper {

    public static HttpClient client = new DefaultHttpClient();

    public String fecthHtmlGet(String url){
        HttpResponse response;
        String responseString = null;
        try {
            response = client.execute(new HttpGet(url));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
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
            if(entity != null)
            {
                String responseBody = EntityUtils.toString(entity);
                return responseBody;
            }
            // System.out.println(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
