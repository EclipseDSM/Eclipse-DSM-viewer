package com.dsmviewer.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public final class PluginUtils {

    private PluginUtils() {
    }

    public static String extractStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static String extractFileName(URL url) {
        String filePath = url.getFile();
        int fileNameBeginIndex = filePath.lastIndexOf('/') + 1;
        return filePath.substring(fileNameBeginIndex);
    }

    public static List<String> listFiles(File dir) {
        List<String> result = new LinkedList<String>();
        for (File file : dir.listFiles()) {
            if (!file.isDirectory()) {
                result.add(file.getAbsolutePath());
            }
        }
        return result;
    }

}
