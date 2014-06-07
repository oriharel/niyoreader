package com.niyo.reader.app;

/**
 * Created by oriharel on 6/7/14.
 */
public abstract class ServiceCaller {

    public abstract void success(Object data);
    public abstract void failure(Object data, String description);
}
