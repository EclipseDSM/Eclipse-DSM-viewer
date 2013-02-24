package com.dsmviewer;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.dsmviewer.logging.Logger;
import com.dsmviewer.logging.LoggerFactory;

/**
 * The Activator Class.
 * 
 * @author Roman Ivanov
 */
public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "DSM-Viewer"; //$NON-NLS-1$

    private static Activator pluginInstance;

    private static LoggerFactory loggerFactory;
    private static Logger logger;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        pluginInstance = this;
        loggerFactory = new LoggerFactory();
        logger = loggerFactory.getLogger(getClass());
        logger.info("DSM-viewer plugin started");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        pluginInstance = null;
        super.stop(context);
        loggerFactory.getLogger(getClass()).info("DSM-viewer plugin stopped");
        loggerFactory = null; // closes all plugin loggers
    }

    /**
     * Returns the current plugin instance.
     * 
     * @return the current shared plugin instance.
     */
    public static Activator getInstance() {
        return pluginInstance;
    }

    /**
     * Gets the plugin id.
     * 
     * @return the plugin id (symbolic name of current bundle).
     */
    public String getPluginId() {
        return pluginInstance.getBundle().getSymbolicName();
    }

    /**
     * Gets an absolute path from given resource relative path.
     * 
     * @param filePath - relative path to any resource.
     * @return the absolute path.
     * @throws IOException
     */
    public static String getAbsolutePath(final String filePath) {
        String result = null;
        URL confUrl = pluginInstance.getBundle().getEntry(filePath);
        try {
            result = FileLocator.toFileURL(confUrl).getFile();
        } catch (IOException e) {
            String errorMessage = "Cannot find the file URL for " + filePath;
            logger.error(errorMessage, e);
            showErrorMessage(errorMessage);
        }
        return result;
    }

    /**
     * Shows the info message.
     * 
     * @param message the message
     */
    public static void showInfoMessage(String message) {
        MessageDialog.openInformation(Display.getDefault().getActiveShell(), PLUGIN_ID, message);
    }

    /**
     * Shows the error message.
     * 
     * @param message the message
     */
    public static void showErrorMessage(String message) {
        MessageDialog.openError(Display.getDefault().getActiveShell(), PLUGIN_ID, message);
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public static Logger getLogger(String className) {
        return loggerFactory.getLogger(className);
    }

    public static <T> Logger getLogger(Class<T> clazz) {
        return loggerFactory.getLogger(clazz);
    }

}
