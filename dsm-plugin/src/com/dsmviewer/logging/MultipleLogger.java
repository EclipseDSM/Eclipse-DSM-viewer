package com.dsmviewer.logging;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class MultipleLogger implements Logger {

    private volatile List<Logger> loggers;

	public MultipleLogger(Logger... loggers) {
		this.loggers = Arrays.asList(loggers);
	}

	@Override
    public synchronized void debug(String message) {
		for (Logger logger : loggers) {
			logger.debug(message);
		}
	}

	@Override
    public synchronized void info(String message) {
		for (Logger logger : loggers) {
			logger.info(message);
		}
	}

	@Override
    public synchronized void warn(String message) {
		for (Logger logger : loggers) {
			logger.warn(message);
		}
	}

	@Override
    public synchronized void warn(String message, Throwable e) {
		for (Logger logger : loggers) {
			logger.warn(message, e);
		}
	}

	@Override
    public synchronized void error(String message, Throwable e) {
		for (Logger logger : loggers) {
			logger.error(message, e);
		}
	}

    public synchronized void addLogger(Logger logger) {
		if (logger != null) {
			loggers.add(logger);
		}
	}

    public synchronized boolean removeLogger(Logger logger) {
		return loggers.remove(logger);
	}

    public synchronized List<Logger> getLoggers() {
		return loggers;
	}

}
