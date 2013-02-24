package com.dsmviewer.logging;

import java.text.MessageFormat;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.dsmviewer.Activator;

public final class Logger {

	private static final String LOGGING_PATTERN = "[{0}][{1}]: {2}";

	private final ILog logger = Activator.getInstance().getLog();

	private String className;

	private Logger() {
		// no code
	}

	private <T> Logger(Class<T> clazz) {
		this.className = clazz.getSimpleName();
	}

	private Logger(String className) {
		this.className = className;
	}


	public static Logger getLogger(String classname) {
		Logger logger = new Logger(classname);
		return logger;
	}

	public static <T> Logger getLogger(Class<T> clazz) {
		Logger logger = new Logger(clazz);
		return logger;
	}

	public void info(String message) {
		int status = IStatus.INFO;
		logger.log(new Status(status, Activator.PLUGIN_ID, formatMsg(status, message)));
	}

	public void warn(String message) {
		int status = IStatus.WARNING;
		logger.log(new Status(status, Activator.PLUGIN_ID, formatMsg(status, message)));
	}

	public void warn(String message, Throwable e) {
		int status = IStatus.WARNING;
		logger.log(new Status(status, Activator.PLUGIN_ID, formatMsg(status, message + e.getMessage()), e));
	}

	public void error(String message, Throwable e) {
		int status = IStatus.ERROR;
		logger.log(new Status(status, Activator.PLUGIN_ID, formatMsg(status, message + e.getMessage()), e));
	}

	private String formatMsg(int level, String message) {
		return MessageFormat.format(LOGGING_PATTERN, this.className, statusToString(level), message);
	}

	private static String statusToString(int level) {
		switch (level) {
		case IStatus.INFO:
			return "INFO";
		case IStatus.WARNING:
			return "INFO";
		case IStatus.ERROR:
			return "INFO";
		default:
			return "INFO";
		}
	}

}
