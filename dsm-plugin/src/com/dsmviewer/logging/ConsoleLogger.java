package com.dsmviewer.logging;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * This logger creates the plugin`s console in Developers Eclipse instance and logs to it.
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public final class ConsoleLogger implements Logger {

	private static final String LOGGING_PATTERN = "[{0}][{1}][{2}]: {3}";

	private static final Color LOG_COLOR_DEFAULT = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

	private static final Color LOG_COLOR_DEBUG = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	private static final Color LOG_COLOR_INFO = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
    private static final Color LOG_COLOR_WARN = new Color(Display.getCurrent(), 232, 174, 91); // orange
	private static final Color LOG_COLOR_ERROR = Display.getCurrent().getSystemColor(SWT.COLOR_RED);

	private final String className;

	private static enum LogLevel {
		DEBUG("DEBUG", LOG_COLOR_DEBUG),
		INFO("INFO", LOG_COLOR_INFO),
		WARN("WARN", LOG_COLOR_WARN),
		ERROR("ERROR", LOG_COLOR_ERROR);

		private String name;
		private Color color;

		private LogLevel(String name, Color color) {
			this.name = name;
			this.color = color;
		}

		public Color getColor() {
			return this.color;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	private <T> ConsoleLogger(Class<T> clazz) {
		this.className = clazz.getSimpleName();
	}

	private ConsoleLogger(String className) {
		this.className = className;
	}

	protected static ConsoleLogger getLogger(String classname) {
		ConsoleLogger logger = new ConsoleLogger(classname);
		return logger;
	}

	protected static <T> ConsoleLogger getLogger(Class<T> clazz) {
		ConsoleLogger logger = new ConsoleLogger(clazz);
		return logger;
	}

	@Override
	public void debug(String message) {
		appendMessage(LogLevel.DEBUG, message, true);
	}

	@Override
	public void info(String message) {
		appendMessage(LogLevel.INFO, message, true);
	}

	@Override
	public void warn(String message) {
		appendMessage(LogLevel.WARN, message, true);
	}

	@Override
	public void warn(String message, Throwable e) {
		String errorMessage = format("{0}: {1}", message, e.getMessage());
		appendMessage(LogLevel.WARN, errorMessage, true);
		appendMessage(extractStackTrace(e), LOG_COLOR_WARN, true);
	}

	@Override
	public void error(String message, Throwable e) {
		String errorMessage = format("{0}: {1}", message, e.getMessage());
		appendMessage(LogLevel.ERROR, errorMessage, true);
		appendMessage(extractStackTrace(e), LOG_COLOR_ERROR, true);
	}

//	private static void appendMessage(String msg, boolean newLine) {
//		appendMessage(msg, LOG_COLOR_DEFAULT, newLine);
//	}

	private void appendMessage(LogLevel level, String msg, boolean newLine) {
		appendMessage(formatMessage(level, msg), level.getColor(), newLine);
	}

	private static void appendMessage(String msg, Color color, boolean newLine) {
		ConsoleStream out = new ConsoleStream();
		if (color == null) {
			out.setColor(LOG_COLOR_DEFAULT);
		} else {
			out.setColor(color);
		}
		try {
			if (newLine) {
				out.println(msg);
			} else {
				out.print(msg);
			}
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String formatMessage(LogLevel level, String message) {
		return format(LOGGING_PATTERN, new Date(), level, className, message);
	}

	private static String extractStackTrace(Throwable e) {
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

}
