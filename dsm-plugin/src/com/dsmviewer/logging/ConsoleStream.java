package com.dsmviewer.logging;

import java.io.IOException;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.dsmviewer.Activator;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class ConsoleStream {

    private final MessageConsoleStream out;
    private final MessageConsole console;


    public ConsoleStream() {
        console = findPluginsConsole(Activator.getPluginId(), true);
        out = console.newMessageStream();
//		out.setActivateOnWrite(true);
    }

    public static final MessageConsole findPluginsConsole(String consoleName, boolean createNewIfNecessary) {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conMan = plugin.getConsoleManager();
        IConsole[] existing = conMan.getConsoles();
        for (IConsole element : existing) {
            if (consoleName.equals(element.getName())) {
                return (MessageConsole) element;
            }
        }
        if (createNewIfNecessary) {
            // no Opened Eclipse Console found for current plugin, create a new one:
            MessageConsole myConsole = new MessageConsole(consoleName, null);
            conMan.addConsoles(new IConsole[] { myConsole });
            return myConsole;
        } else {
            return null;
        }
    }

    public void print(String message) {
        out.print(message);
    }

    public void println(String message) {
        out.println(message);
    }

    /**
     * Displays Eclipse console for current plugin (if it is currently not shown).
     * 
     * @param page current active workBench page
     * @throws PartInitException
     */
    public void showConsole() throws PartInitException {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IConsoleView view = (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
        view.display((IConsole) console);
    }

    public void setColor(Color newColor) {
        out.setColor(newColor);
    }

    public void close() throws IOException {
        out.close();
    }

}
