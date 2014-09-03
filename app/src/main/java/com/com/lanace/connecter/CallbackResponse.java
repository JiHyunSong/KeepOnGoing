package com.com.lanace.connecter;

import org.apache.http.HttpResponse;

/**
 * Created by Lanace on 2014-09-02.
 */
public abstract class CallbackResponse {
    public abstract void success(HttpResponse httpResponse);

    public abstract void error(Exception e);
}
