package com.dsmviewer;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.dsmviewer.ui.views.DSMView;

/**
 * The Activator Class.
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 */
public class Activator extends AbstractUIPlugin {

    /** The plug-in ID. */
    public static final String PLUGIN_ID = "DSM-viewer"; //$NON-NLS-1$

    /** The plugin shared instance. */
    private static Activator plugin;

	@Override
	public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

	@Override
	public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }
   
    /**
     * Returns the current plugin instance.
     * 
     * @return the current shared plugin instance.
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
     * Gets an absolute path from given resource relative path.
     * 
     * @param filePath
     *            - relative path to any resource.
     * @return the absolute path.
     * @throws IOException
     */
    public static String getAbsolutePath(final String filePath) {
        String result = null;
        URL confUrl = getInstance().getBundle().getEntry(filePath);
        try {
            result = FileLocator.toFileURL(confUrl).getFile();
        } catch (IOException e) {
            DSMView.showErrorMessage("Cannot find the file URL for " + filePath);
        }
        return result;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

}
