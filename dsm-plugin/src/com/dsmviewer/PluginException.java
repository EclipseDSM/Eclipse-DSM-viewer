package com.dsmviewer;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class PluginException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PluginException(String message, Throwable e) {
        super(message, e);
    }

    public PluginException(String message) {
        super(message);
    }

}
