package com.dsmviewer.logging;

import static java.text.MessageFormat.format;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.dsmviewer.Activator;

/**
 * This class is an adapter to Eclipse native logging. It adds a 'DEBUG' level and log messages formatting by pattern.
 * There are 2 modes for this adapter, between which you can switch using '-Ddsmviewer.debugMode' JVM property: 1. Debug
 * mode. 2. Production mode. Logger shows only logs with level higher or equal of 'INFO'
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public final class NativeLogger implements Logger {

    private static final String LOGGING_PATTERN = "[{0}][{1}]: {2}";

    private final ILog logger = Activator.getInstance().getLog();

    private String className;

    private boolean isDebugMode;

    // Show all logs by default
    private int ignoreLevel = IStatus.INFO;

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

    // Hack to support 'debug' level which doesn`t supported at all by Eclipse native logging 
    @Override
    public void debug(String message) {
        if (IStatus.INFO >= ignoreLevel) {
            if (isDebugMode) {
                String formattedMessage = formatMsg("DEBUG", message);
                logger.log(buildMessage(IStatus.INFO, formattedMessage));
            } else {
                logger.log(buildMessage(IStatus.INFO, message));
            }
        }
    }

    @Override
    public void info(String message) {
        if (IStatus.INFO >= ignoreLevel) {
            log(IStatus.INFO, message);
        }
    }

    @Override
    public void warn(String message) {
        if (IStatus.WARNING >= ignoreLevel) {
            log(IStatus.WARNING, message);
        }
    }

    @Override
    public void warn(String message, Throwable e) {
        if (IStatus.WARNING >= ignoreLevel) {
            log(IStatus.WARNING, message, e);
        }
    }

    @Override
    public void error(String message, Throwable e) {
        if (IStatus.ERROR >= ignoreLevel) {
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
            logger.log(buildMessage(status, formattedMessage));
        } else {
            logger.log(buildMessage(status, message));
        }
    }

    private static Status buildMessage(int status, String message) {
        return new Status(status, Activator.PLUGIN_ID, message);
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
        return ignoreLevel;
    }

    public void setIgnoreLevel(int ignoreLevel) {
        this.ignoreLevel = ignoreLevel;
    }

}
