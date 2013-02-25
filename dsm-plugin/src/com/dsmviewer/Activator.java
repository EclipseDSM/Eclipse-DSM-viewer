package com.dsmviewer;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.dsmviewer.logging.ConsoleStream;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.logging.LoggerFactory;
import com.dsmviewer.utils.CoreUtils;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "DSM-Viewer"; //$NON-NLS-1$

    private static ImageRegistry imageRegistry;

    private static LoggerFactory loggerFactory;
    private static Logger logger;

    private static Activator pluginInstance;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        pluginInstance = this;

        imageRegistry = new ImageRegistry();
        loadImagesToRegistry();

        loggerFactory = new LoggerFactory();
        logger = loggerFactory.getLogger(getClass());
        logger.info("DSM-viewer plugin started");
    }

    private static void loadImagesToRegistry() {
        Bundle bundle = pluginInstance.getBundle();

        Enumeration<URL> entries = bundle.findEntries("/icons/", "*.gif", true);
//        IPath path = new Path("");
//        URL[] iconPaths = FileLocator.findEntries(bundle, path);
        while (entries.hasMoreElements()) {
            URL url = entries.nextElement();
            ImageDescriptor desc = ImageDescriptor.createFromURL(url);
            imageRegistry.put(CoreUtils.extractFileName(url), desc);
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        loggerFactory.getLogger(getClass()).info("Stopping DSM-viewer plugin...");

        imageRegistry.dispose();
        imageRegistry = null;

        // cleaning for logging
        if (loggerFactory.isDebugMode()) {
            // destroy plugin`s target debug console
            MessageConsole targetConsole = ConsoleStream.findPluginsConsole(getPluginId(), false);
            if (targetConsole != null) {
                targetConsole.destroy();
            }
        }
        loggerFactory = null; // destroy all created loggers

        pluginInstance = null;

        super.stop(context);
    }

    /**
     * Returns the current plugin instance.
     * 
     * @return the current shared plugin instance.
     */
    public static Activator getInstance() {
        return pluginInstance;
    }

    public static String getPluginId() {
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
            String errorMessage = "Cannot retrieve absolute path for the file: " + filePath;
            logger.error(errorMessage, e);
            showErrorMessage(errorMessage, e);
        }
        return result;
    }

// code to retrieve an java.io.InputStream
//    InputStream inputStream = FileLocator.openStream(
//        Activator.getDefault().getBundle(), new Path("resources/setup.xml"), false);

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
     * Shows the error message with appropriate error stacktrace.
     * 
     * @param message the message
     */
    public static void showErrorMessage(String message, Throwable e) {
        StringBuilder sb = new StringBuilder(message);
        sb.append(":\n");
        sb.append(CoreUtils.extractStackTrace(e));
        MessageDialog.openError(Display.getDefault().getActiveShell(), PLUGIN_ID, sb.toString());
    }

    public static Image getImageFromRegistry(String filename) {
        return (imageRegistry == null) ? null : imageRegistry.get(filename);
    }

//    /**
//     * Returns an image descriptor for the image file at the given plug-in relative path.
//     * 
//     * @param path the path
//     * @return the image descriptor
//     */
//    public static ImageDescriptor getImageDescriptor(String path) {
//        return imageDescriptorFromPlugin(PLUGIN_ID, path);
//    }

    public static Logger getLogger(String className) {
        return loggerFactory.getLogger(className);
    }

    public static <T> Logger getLogger(Class<T> clazz) {
        return loggerFactory.getLogger(clazz);
    }

}
