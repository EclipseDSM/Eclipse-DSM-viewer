package com.dsmviewer.maven;

import java.io.File;
import java.util.List;

import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public final class MavenRunner {

    private MavenRunner() {
    }

//  public static void runMaven(IFile pomFile, List<String> goals,
//  IProgressMonitor monitor) {
//
//  InvocationRequest request = new DefaultInvocationRequest();
//  try {
//  File file = new File(pomFile.getLocationURI());
//  request.setPomFile(file);
//  request.setBaseDirectory(new File(pomFile.getProject().getLocationURI()));
//  request.setGoals(goals);
//  request.setShowErrors(true);
//
//  Invoker invoker = new DefaultInvoker();
//  invoker.setMavenHome(new File("/Users/danijoh2/maven3")); // This will not do!
//  InvocationResult result = invoker.execute(request);
//  if (result.getExitCode() != 0 ){
//  if (result.getExecutionException() != null ) { 
//      //Exception
//      }
//  }
//  else { 
//      //Exception                 
//  }
//  }
//  catch (MavenInvocationException e) { //Exception 
//      }
//  }
//  }

    public static void runMaven(IFile pomFile, List<String> goals, IProgressMonitor monitor) throws CoreException {
        IMaven maven = MavenPlugin.getMaven();
        // MavenPlugin.getDefault().getConsole().showConsole();
        File file = new File(pomFile.getLocationURI());
        File projectDirectory = new File(pomFile.getProject().getLocationURI());
        MavenExecutionRequest request = maven.createExecutionRequest(monitor);
        request.setBaseDirectory(projectDirectory);
        request.setPom(file);
        request.setGoals(goals);
        request.setShowErrors(true);

        MavenExecutionResult result = maven.execute(request, monitor);
    }
}
