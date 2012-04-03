package com.dsmviewer.dtangler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dsmengine.DsmEngine;
import org.dtangler.core.textui.DSMWriter;
import org.dtangler.core.textui.SysoutWriter;
import org.dtangler.core.textui.ViolationWriter;
import org.dtangler.core.textui.Writer;

import com.dsmviewer.ui.views.DSMView;

public class MyFileWriter implements Writer {  

    PrintWriter fw = null;

    public MyFileWriter(String filePath) {
        try {
            fw = new PrintWriter(new FileWriter(new File(filePath)));
        } catch (IOException e) {
            DSMView.showErrorMessage("Cannot write data to file " + filePath + ": " + e.getMessage());
        }
    }

    @Override
    public void print(String s) {
        fw.print(s);
    }

    @Override
    public void println(String s) {
        fw.println(s);        
    }

    public void close() {
        if (fw != null) {
            fw.close();
        }
    }
    
}
