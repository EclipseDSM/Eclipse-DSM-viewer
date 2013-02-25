package com.dsmviewer.logging;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;

/**
 * Factory for creating {@link com.dsmviewer.logging.Logger} instances. Searches for -Ddsmviewer.debugMode=[true|false]
 * JVM property:
 * 
 * <ol>
 * <li>In Debug mode (if dsmviewer.debugMode=true) runtime plugin logs to it`s own console and Eclipse native log shows
 * all plugin logs for all levels</li>
 * <li>In Production mode (if dsmviewer.debugMode=false) runtime plugin doesn`t log to it`s own console and Eclipse
 * native log shows 'WARNING' and 'ERROR' logs only</li>
 * </ol>
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class LoggerFactory {

    // all loggers created in plugin <CLASS_NAME, LOGGER>
    private final Map<String, Logger> loggers = new HashMap<String, Logger>();

//  In 'debug mode': runtime plugin logs to it`s own console; Eclipse native log shows all plugin logs for all levels.
//  Otherwise runtime plugin doesn`t log to it`s own console; Eclipse native log shows 'WARNING' and 'ERROR' logs only
    private boolean isDebugMode;

    public LoggerFactory() {
        this.isDebugMode = "true".equals(System.getProperty("dsmviewer.debugMode"));
    }

    public <T> Logger getLogger(Class<T> clazz) {
        Logger existingLogger = loggers.get(clazz.getName());
        if (existingLogger != null) {
            return existingLogger;
        } else {
            return createLogger(clazz.getSimpleName());
        }
    }

    public Logger getLogger(String className) {
        Logger existingLogger = loggers.get(className);
        if (existingLogger != null) {
            return existingLogger;
        } else {
            return createLogger(className);
        }
    }

    private Logger createLogger(String className) {
        Logger result;
        NativeLogger nativeLogger = NativeLogger.getLogger(className, isDebugMode);
        if (isDebugMode) {
            ConsoleLogger consoleLogger = ConsoleLogger.getLogger(className);
            MultipleLogger multipleLogger = new MultipleLogger(nativeLogger, consoleLogger);
            loggers.put(className, multipleLogger);
            result = multipleLogger;
        } else {
            nativeLogger.setIgnoreLevel(IStatus.WARNING);
            loggers.put(className, nativeLogger);
            result = nativeLogger;
        }
        return result;
    }

    public boolean isDebugMode() {
        return isDebugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.isDebugMode = debugMode;
    }

}
