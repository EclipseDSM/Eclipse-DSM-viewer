package com.dsmviewer.logging;

import java.util.Arrays;
import java.util.List;

public class MultipleLogger implements Logger {

	private List<Logger> loggers;

	public MultipleLogger(Logger... loggers) {
		this.loggers = Arrays.asList(loggers);
	}

	@Override
	public void debug(String message) {
		for (Logger logger : loggers) {
			logger.debug(message);
		}
	}

	@Override
	public void info(String message) {
		for (Logger logger : loggers) {
			logger.info(message);
		}
	}

	@Override
	public void warn(String message) {
		for (Logger logger : loggers) {
			logger.warn(message);
		}
	}

	@Override
	public void warn(String message, Throwable e) {
		for (Logger logger : loggers) {
			logger.warn(message, e);
		}
	}

	@Override
	public void error(String message, Throwable e) {
		for (Logger logger : loggers) {
			logger.error(message, e);
		}
	}

	public void addLogger(Logger logger) {
		if (logger != null) {
			loggers.add(logger);
		}
	}

	public boolean removeLogger(Logger logger) {
		return loggers.remove(logger);
	}

	public List<Logger> getLoggers() {
		return loggers;
	}

}
