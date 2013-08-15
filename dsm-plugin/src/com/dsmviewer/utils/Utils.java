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
public final class Utils {

    private Utils() {
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

    /**
     * @return - true if both objects are null; <br>
     *         - result of null-safe "equals()" operation otherwise.
     */
    public static boolean nullSafeEquals(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        } else {
            return a == null ? b.equals(a) : a.equals(b);
        }
    }

    public static String convertStreamToString(java.io.InputStream is) {
        if (is != null) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
        return null;
    }
    
	public static int getRandomInt(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}
    
    
}
