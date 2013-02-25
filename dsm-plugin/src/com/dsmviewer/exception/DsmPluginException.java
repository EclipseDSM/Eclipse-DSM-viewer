package com.dsmviewer.exception;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class DsmPluginException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DsmPluginException(String message, Throwable e) {
        super(message, e);
    }

    public DsmPluginException(String message) {
        super(message);
    }

}
