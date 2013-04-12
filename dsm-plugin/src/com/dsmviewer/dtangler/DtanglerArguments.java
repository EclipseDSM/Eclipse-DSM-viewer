package com.dsmviewer.dtangler;

import java.util.List;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.input.ArgumentBuilder;

/**
 * 
 * @author Roman Ivanov
 * 
 */
public class DtanglerArguments extends Arguments {

    /**
     * Builds the Arguments from the string contains Dtangler command-line arguments. See <a
     * href="http://web.sysart.fi/dtangler/documentation">Dtangler documentation</a> for more information about it`s
     * command line parameters.
     * 
     * @param commandLineArguments - input string.
     * @return Arguments that are appropriable for Dtangler java engine.
     * 
     */
    public static Arguments build(String commandLineArguments) {
        return new ArgumentBuilder().build(new String[] { commandLineArguments });
    }

    /**
     * Builds the Arguments from pathList.
     * 
     * @param pathList - list contains paths of input files/directories/jars that will be be parsed by Dtangler.
     * @param scope the parsing scope. Supported values: "locations", "packages", "classes".
     * @param cyclesAllowed - defines whether cyclic dependencies will fail the Dtangler cycles analyzing.
     * 
     * @return Arguments that are appropriable for Dtangler java engine.
     * @see <a href="http://web.sysart.fi/dtangler/builds/dtangler-r15064_Release-2.0.0/
     *      release/documentation/documentation.html">Dtangler documentation</a> *
     */
    public static Arguments build(List<String> pathList, String scope, boolean cyclesAllowed) {
        Arguments arguments = new Arguments();
        arguments.setInput(pathList);
        arguments.setScope(scope);
        arguments.setCyclesAllowed(cyclesAllowed);
        return arguments;
    }

}
