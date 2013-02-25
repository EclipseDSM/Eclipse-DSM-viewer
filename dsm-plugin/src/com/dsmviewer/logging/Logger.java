package com.dsmviewer.logging;

/**
 * Logging levels sorted by importance (asc.):
 * 
 * <pre>
 * <ul>
 * <li>DEBUG.</li>
 * <li>INFO.</li>
 * <li>WARNING.</li>
 * <li>ERROR.</li>
 * </ul>
 * </pre>
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public interface Logger {

    /**
     * Logs given message as DEBUG level.
     * 
     * @param message given message (message will be formatted in different ways by different loggers)
     */
    void debug(String message);

    /**
     * Logs given message as INFO level.
     * 
     * @param message given message (message will be formatted in different ways by different loggers)
     */
    void info(String message);

    /**
     * Logs given message as WARNING level.
     * 
     * @param message given message (message will be formatted in different ways by different loggers)
     */
    void warn(String message);

    /**
     * Logs given message as WARNING level.
     * 
     * @param message given message (message will be formatted in different ways by different loggers)
     * @param e - Exception to log.
     */
    void warn(String message, Throwable e);

    /**
     * Logs given message as ERROR level.
     * 
     * @param message given message (message will be formatted in different ways by different loggers)
     * @param e - Exception to log.
     */
    void error(String message, Throwable e);

}