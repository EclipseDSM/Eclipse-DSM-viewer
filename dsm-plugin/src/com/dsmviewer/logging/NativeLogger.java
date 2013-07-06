package com.dsmviewer.logging;

import static java.text.MessageFormat.format;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.dsmviewer.Activator;

/**
 * Proxy for Eclipse native logging. It adds a 'DEBUG' level and log messages formatting by pattern. There are 2 modes
 * for NativeLogger, between which you can switch using '-Ddsmviewer.debugMode' JVM property: 1. Debug mode. 2.
 * Production mode, when Logger shows only logs with level higher or equal of 'INFO'
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public final class NativeLogger implements Logger {

    private static final String LOGGING_PATTERN = "[{0}][{1}]: {2}";

    private final ILog eclipseNativeLogger = Activator.getInstance().getEclipseNativeLogger();

    private String className;

    // Shows all logs by default
    private int defaultIgnoreLevel = IStatus.INFO;

    private boolean isDebugMode;

    private NativeLogger() {
    }

    private <T> NativeLogger(Class<T> clazz, boolean isDebugMode) {
        this.className = clazz.getSimpleName();
        this.isDebugMode = isDebugMode;
    }

    private NativeLogger(String className, boolean isDebugMode) {
        this.className = className;
        this.isDebugMode = isDebugMode;
    }

    protected static NativeLogger getLogger(String classname, boolean isDebugMode) {
        return new NativeLogger(classname, isDebugMode);
    }

    protected static <T> NativeLogger getLogger(Class<T> clazz, boolean isDebugMode) {
        return new NativeLogger(clazz, isDebugMode);
    }

    // Hack to support the 'debug' level which doesn`t supported by Eclipse native logger
    @Override
    public synchronized void debug(String message) {
        if (IStatus.INFO >= defaultIgnoreLevel) {
            if (isDebugMode) {
                String formattedMessage = formatMsg("DEBUG", message);
                eclipseNativeLogger.log(buildMessage(IStatus.INFO, formattedMessage));
            } else {
                eclipseNativeLogger.log(buildMessage(IStatus.INFO, message));
            }
        }
    }

    @Override
    public synchronized void info(String message) {
        if (IStatus.INFO >= defaultIgnoreLevel) {
            log(IStatus.INFO, message);
        }
    }

    @Override
    public synchronized void warn(String message) {
        if (IStatus.WARNING >= defaultIgnoreLevel) {
            log(IStatus.WARNING, message);
        }
    }

    @Override
    public synchronized void warn(String message, Throwable e) {
        if (IStatus.WARNING >= defaultIgnoreLevel) {
            log(IStatus.WARNING, message, e);
        }
    }

    @Override
    public synchronized void error(String message, Throwable e) {
        if (IStatus.ERROR >= defaultIgnoreLevel) {
            log(IStatus.ERROR, message, e);
        }
    }

    private void log(int status, String message, Throwable e) {
        String errorMessage = format("{0}: {1}", message, e.getMessage());
        log(status, errorMessage);
    }

    private void log(int status, String message) {
        if (isDebugMode) {
            String formattedMessage = formatMsg(status, message);
            eclipseNativeLogger.log(buildMessage(status, formattedMessage));
        } else {
            eclipseNativeLogger.log(buildMessage(status, message));
        }
    }

    private static Status buildMessage(int status, String message) {
        return new Status(status, Activator.getPluginId(), message);
    }

    private String formatMsg(int severity, String message) {
        return formatMsg(severityToString(severity), message);
    }

    private String formatMsg(String status, String message) {
        return format(LOGGING_PATTERN, this.className, status, message);
    }

    private static String severityToString(int severity) {
        // @formatter:off
		switch (severity) {
			case IStatus.INFO: return "INFO";
			case IStatus.WARNING: return "WARNING";
			case IStatus.ERROR: return "ERROR";
		    default: return "<UNKNOWN_LEVEL>";
		}
		// @formatter:on
    }

    public boolean isDebugMode() {
        return isDebugMode;
    }

    public void setDebugMode(boolean isDebugMode) {
        this.isDebugMode = isDebugMode;
    }

    public int getIgnoreLevel() {
        return defaultIgnoreLevel;
    }

    public void setIgnoreLevel(int ignoreLevel) {
        this.defaultIgnoreLevel = ignoreLevel;
    }

}
