package com.dsmviewer;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.runtime.ILog;
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
import com.dsmviewer.utils.Utils;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "Eclipse-DSM-Viewer"; //$NON-NLS-1$

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
        logger = loggerFactory.getLogger(Activator.class.getName());
        logger.info("DSM-viewer plugin started");
    }

    private static void loadImagesToRegistry() {
        Bundle bundle = pluginInstance.getBundle();

        Enumeration<URL> entries = bundle.findEntries("/icons/", "*.*", true);
        while (entries.hasMoreElements()) {
            URL url = entries.nextElement();
            ImageDescriptor desc = ImageDescriptor.createFromURL(url);
            imageRegistry.put(Utils.extractFileName(url), desc);
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        logger.info("Stopping DSM-viewer plugin...");

        imageRegistry.dispose();
        imageRegistry = null;

        // destroy target plugin`s debug console
        if (loggerFactory.isDebugMode()) {
            MessageConsole targetConsole = ConsoleStream.findPluginsConsole(getPluginId(), false);
            if (targetConsole != null) {
                targetConsole.destroy();
            }
        }
        loggerFactory = null; // this will destroy all created loggers
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
        sb.append(Utils.extractStackTrace(e));
        MessageDialog.openError(Display.getDefault().getActiveShell(), PLUGIN_ID, sb.toString());
    }

    public static Image getImageFromRegistry(String filename) {
        return imageRegistry.get(filename);
    }

    public static ImageDescriptor getImageDescriptorFromRegistry(String imageNameOrPath) {
        String pluginId = getPluginId();
        ImageDescriptor imageDescriptor = imageDescriptorFromPlugin(pluginId, imageNameOrPath);
        if (imageDescriptor == null) {
            imageDescriptor = imageDescriptorFromPlugin(pluginId, "icons" + File.separator + imageNameOrPath);
        }
        return imageDescriptor;
    }

    public ILog getEclipseNativeLogger() {
        return getLog();
    }

    public static Logger getLogger(String className) {
        return loggerFactory.getLogger(className);
    }

    public static <T> Logger getLogger(Class<T> clazz) {
        return loggerFactory.getLogger(clazz);
    }

}
