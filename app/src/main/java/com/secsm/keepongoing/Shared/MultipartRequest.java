package com.secsm.keepongoing.Shared;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.mime.MultipartEntity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@SuppressWarnings("rawtypes")
public class MultipartRequest extends Request<Map> {

    private MultipartEntity entity = new MultipartEntity();
    private static final Gson g_gson = new Gson();

//    private final ErrCheckListener mListener;

//    public MultipartRequest(int method, String url, MultipartEntity params, ErrCheckListener listener, Response.ErrorListener errorListener)
//    {
//        super(method, url, errorListener);
//        mListener = listener;
//        entity = params;
//    }
//
    public MultipartRequest(int method, String url, MultipartEntity params, Response.ErrorListener errorListener)
    {
        super(method, url, errorListener);
        entity = params;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        Map<String, String> headers = super.getHeaders();

        if ( headers == null || headers.equals(Collections.emptyMap()) )
        {
            headers = new HashMap<String, String>();
        }
        headers.put("Connection", "Keep-Alive");
        headers.put("Accept-Charset", "UTF-8");
        headers.put("ENCTYPE", "multipart/form-data");
        return headers;
    }

    @Override
    public String getBodyContentType()
    {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            entity.writeTo(bos);
        }
        catch (IOException e)
        {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

//    @SuppressWarnings("unchecked")
//    @Override
//    protected void deliverResponse(Map response)
//    {
//        mListener.onResponse(response);
//    }

    @SuppressWarnings("unchecked")
    @Override
    protected void deliverResponse(Map response)
    {
//        mListener.onResponse(response);
    }


    @SuppressWarnings("rawtypes")
    @Override
    protected Response<Map> parseNetworkResponse(NetworkResponse response)
    {
        try
        {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
//            Log.i("MultipartRequest", "response.data : " + new String(response.data, "UTF-8"));
//            Log.i("MultipartRequest", "response.headers : " + response.headers);

            return Response.success(g_gson.fromJson(json, Map.class), HttpHeaderParser.parseCacheHeaders(response));
        }
        catch (UnsupportedEncodingException e)
        {
            return Response.error(new ParseError(e));
        }
        catch (JsonSyntaxException e)
        {
            return Response.error(new ParseError(e));
        }
    }
}