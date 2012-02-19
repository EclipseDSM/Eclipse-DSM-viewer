package com.dsmviewer.dtangler;

import java.util.List;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.input.ArgumentBuilder;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavtsev</a>
 *
 */
public class DtanglerArguments extends Arguments {
       
    /**
     * 
     * @param commandLineArguments - command line arguments
     * @return Arguments for Dtangler java engine.
     * 
     * @see <a href="http://web.sysart.fi/dtangler/documentation">Dtangler documentation</a>
     */
    public static Arguments build(String commandLineArguments){
        return new ArgumentBuilder().build(new String[] {commandLineArguments});
    }

    /**
     * 
     * @param pathList - list of the input files/directories/jars paths.
     * @return Arguments for Dtangler java engine.
     * 
     * @see <a href="http://web.sysart.fi/dtangler/builds/dtangler-r15064_Release-2.0.0/
     * release/documentation/documentation.html">Dtangler documentation</a>
     */
    public static Arguments build(List<String> pathList){
        Arguments arguments = new Arguments();    
        arguments.setInput(pathList);
        return arguments;
    }
    
}
