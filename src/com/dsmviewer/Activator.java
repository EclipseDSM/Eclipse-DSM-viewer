package com.dsmviewer;

import java.io.IOException;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class Activator.
 *
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 */
public class Activator extends AbstractUIPlugin {

    /** The plug-in ID. */
    public static final String PLUGIN_ID = "DSM-viewer"; //$NON-NLS-1$

    /**
     * Log4j properties file path.
     */
    private static final String LOG4J_PROPERTIES_FILE_PATH = "configs/log4j.properties";

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** The plugin shared instance. */
    private static Activator plugin;

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        PropertyConfigurator.configure(getAbsolutePath(LOG4J_PROPERTIES_FILE_PATH));
        logger.info("Log4j configuration was successfully loaded.");
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared plugin instance.
     * 
     * @return the shared plugin instance.
     */
    public static Activator getInstance() {
        return plugin;
    }

    /**
     * Gets the plugin id.
     *
     * @return the plugin id.
     */
    public String getPluginId() {
        return plugin.getBundle().getSymbolicName();
    }
    
    /**
     * Gets the absolute path.
     *
     * @param filePath the file path
     * @return the absolute path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static String getAbsolutePath(final String filePath) throws IOException {
        URL confUrl = getInstance().getBundle().getEntry(filePath);
        return FileLocator.toFileURL(confUrl).getFile();
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

    public Logger getLogger() {
        return logger;
    }
}
