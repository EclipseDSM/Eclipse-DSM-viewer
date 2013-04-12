package com.dsmviewer.utils;

import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import com.dsmviewer.Activator;
import com.dsmviewer.exception.DsmPluginException;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.ui.views.DsmView;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public final class EclipseUtils {

    private static final Logger LOGGER = Activator.getLogger(EclipseUtils.class);

    private EclipseUtils() {
    }


    public static void showDsmView(IWorkbenchPart workbenchPart) throws PartInitException {
        workbenchPart.getSite().getPage().showView(DsmView.ID);
    }

    /**
     * Gets the full path of the given Eclipse Project Explorer resource (Project/File/Folder etc).
     * 
     * @param resource - the resource.
     * @return the full path of the given resource.
     */
    public static String getFullPath(IResource resource) {
        return resource.getLocationURI().getPath().toString();
    }

    /**
     * Gets the 'OutputLocation' path for project is opened in workspace
     */
    public static String getBinaryOutputLocation(String projectName, boolean relativePath, boolean includeProjectName) {
        IProject openedProject = getWorkspaceProject(projectName);
        return getBinaryOutputLocation(openedProject, relativePath, includeProjectName);
    }

    public static String getBinaryOutputLocation(IProject project, boolean relativePath, boolean includeProjectName) {
        IJavaProject javaProject = JavaCore.create(project);
        if (javaProject != null && javaProject.exists()) {
            String result = getBinaryOutputLocation(javaProject, relativePath, includeProjectName);
            return result;
        } else {
            return null;
        }
    }

    public static String getBinaryOutputLocation(IJavaProject javaProject, boolean relativePath,
            boolean includeProjectName) {
        String result;
        try {
            result = javaProject.getOutputLocation().toOSString();
            if (!includeProjectName) {
                String prefix = "/" + javaProject.getElementName();
                result = result.replaceFirst(prefix, "");
            }
            if (!relativePath) {
                result = javaProject.getCorrespondingResource().getLocationURI().getRawPath() + result;
            }
        } catch (JavaModelException e) {
            String message = MessageFormat.format(
                    "Cannot retrieve binary output location for project ''{0}''", javaProject.getElementName());
            LOGGER.error(message, e);
            throw new DsmPluginException(message, e);
        }
        return result;
    }

    /// Build projects

    /**
     * Builds the project (using INCREMENTAL_BUILD) without progress showing
     * 
     * @param project
     * @throws CoreException
     */
    public static void buildProject(IProject project) throws CoreException {
        buildProject(project, new NullProgressMonitor());
    }

    /**
     * Builds the project (using INCREMENTAL_BUILD)
     * 
     * @param project
     * @param progressMonitor
     * @throws CoreException
     */
    public static void buildProject(IProject project, IProgressMonitor progressMonitor) throws CoreException {
        project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, progressMonitor);
    }

    /// ~

    /// Get Eclipse resources

//    public static IType getTypeFromOpenedJavaProject(String javaProjectName, String resourceName) {
//        IProject project = getWorkspaceProject(javaProjectName);
//        if (project == null) {
//            return null;
//        } else {
//            IJavaProject javaProject = JavaCore.create(project);
//            return getType(javaProject, resourceName);
//        }
//    }
//
//    public static IType getType(IJavaProject project, String resourceName) {
//        IType result = null;
//        // SUPPRESS CHECKSTYLE Bug in the ConditionNeedOptimization check! We should fix it soon!
//        if (project != null && project.exists() && project.isOpen() && resourceName != null) {
//            try {
//                 result = project.findType(resourceName);
//            } catch (JavaModelException e) {
//                String message = MessageFormat.format(
//                        "Cannot retrieve type ''{0}'' for project ''{1}''", resourceName, project.getElementName());
//                throw new DsmPluginException(message, e);
//            }
//        }
//        return result;
//    }

    public static IProject getWorkspaceProjectForResource(IResource resource) {
        IProject project = resource.getProject();
        if (project.exists() && project.isOpen()) {
            return project;
        } else {
            return null;
        }
    }

    /**
     * Gets the project is currently present in workspace (in opened/closed state) by it`s short name.
     * 
     * @param name
     * @return
     * @throws CoreException
     */
    public static IProject getWorkspaceProject(String name) {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
        if (project.exists()) {
            return project;
        }
        else {
            return null;
        }
    }


    /// ~

//    public static void visitAllChildResources(IProject project) {
//        try {
//            project.accept(new IResourceVisitor() {
//                @Override
//                public boolean visit(IResource resource) throws CoreException {
//                    return true;
//                }
//            });
//        } catch (CoreException e) {
//        }
//    }

}
