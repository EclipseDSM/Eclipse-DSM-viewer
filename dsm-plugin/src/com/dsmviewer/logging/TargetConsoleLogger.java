package com.dsmviewer.logging;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.util.Date;

import org.eclipse.swt.graphics.Color;

import com.dsmviewer.Activator;
import com.dsmviewer.ui.UiHelper;
import com.dsmviewer.utils.Utils;

/**
 * This logger logs into its own console in target Eclipse instance (if appropriate console doesn`t exists on target
 * platform, it will be initialized and shown on first message showing).
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public final class TargetConsoleLogger implements Logger {

    private static final String LOGGING_PATTERN = "[{0}][{1}][{2}]: {3}";

    private final String className;

    private static enum LogLevel {
        DEBUG("DEBUG", UiHelper.LOG_COLOR_DEBUG),
        INFO("INFO", UiHelper.LOG_COLOR_INFO),
        WARN("WARN", UiHelper.LOG_COLOR_WARN),
        ERROR("ERROR", UiHelper.LOG_COLOR_ERROR);

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

    private <T> TargetConsoleLogger(Class<T> clazz) {
        this.className = clazz.getSimpleName();
    }

    private TargetConsoleLogger(String className) {
        this.className = className;
    }

    protected static TargetConsoleLogger getLogger(String classname) {
        TargetConsoleLogger logger = new TargetConsoleLogger(classname);
        return logger;
    }

    protected static <T> TargetConsoleLogger getLogger(Class<T> clazz) {
        TargetConsoleLogger logger = new TargetConsoleLogger(clazz);
        return logger;
    }

    @Override
    public synchronized void debug(String message) {
        appendMessage(LogLevel.DEBUG, message, true);
    }

    @Override
    public synchronized void info(String message) {
        appendMessage(LogLevel.INFO, message, true);
    }

    @Override
    public synchronized void warn(String message) {
        appendMessage(LogLevel.WARN, message, true);
    }

    @Override
    public synchronized void warn(String message, Throwable e) {
        String errorMessage = format("{0}: {1}", message, e.getMessage());
        appendMessage(LogLevel.WARN, errorMessage, true);
        appendMessage(Utils.extractStackTrace(e), LogLevel.WARN.getColor(), true);
    }

    @Override
    public synchronized void error(String message, Throwable e) {
        String errorMessage = format("{0}: {1}", message, e.getMessage());
        appendMessage(LogLevel.ERROR, errorMessage, true);
        appendMessage(Utils.extractStackTrace(e), LogLevel.ERROR.getColor(), true);
    }

    private void appendMessage(LogLevel level, String msg, boolean addNewLine) {
        appendMessage(formatMessage(level, msg), level.getColor(), addNewLine);
    }

    private static void appendMessage(String msg, Color color, boolean addNewLine) {
        ConsoleStream out = new ConsoleStream();
        if (color == null) {
            out.setColor(UiHelper.LOG_COLOR_DEFAULT);
        } else {
            out.setColor(color);
        }
        try {
            if (addNewLine) {
                out.println(msg);
            } else {
                out.print(msg);
            }
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                String message = TargetConsoleLogger.class.getSimpleName()
                        + ": Error while closing the target ConsoleStream";
                NativeLogger.getLogger(TargetConsoleLogger.class, false).error(message, e);
                Activator.showErrorMessage(message, e);
            }
        }
    }

    private String formatMessage(LogLevel level, String message) {
        return format(LOGGING_PATTERN, new Date(), level, className, message);
    }

}
