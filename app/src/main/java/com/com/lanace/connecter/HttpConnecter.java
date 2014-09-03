package com.com.lanace.connecter;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by Lanace on 2014-09-02.
 */
public class HttpConnecter {

    public static final String HOST = "210.118.74.195";
    public static String REST_URL = "KOG_Server_Rest/rest/";
    public static final int PORT = 8080;

    private static HttpConnecter instance = null;

    private HttpClient httpClient = null;

    private HttpConnecter() {
        httpClient = new DefaultHttpClient();
    }

    public static HttpConnecter getInstance() {
        if (instance == null) {
            instance = new HttpConnecter();
        }

        return instance;
    }

    public synchronized HttpResponse excute(final HttpRequestBase httpRequest)
            throws IOException {
        // httpRequest.addHeader("api_key", "rakkan");
        HttpResponse res = httpClient.execute(httpRequest);
        return res;
    }

//	public static final String getBaseURL() {
//		return "http://" + HOST + ":" + PORT;
//	}

    public static final String getRestfullBaseURL() {
        return "http://" + HOST + ":" + PORT + "/" + REST_URL;
    }

    public static void close() {
        instance = null;
    }

}
