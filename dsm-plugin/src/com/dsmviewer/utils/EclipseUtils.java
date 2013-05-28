package com.dsmviewer.utils;

import java.io.File;
import java.text.MessageFormat;

import org.dtangler.core.dependencies.Dependable;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.part.FileEditorInput;

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

    /**
     * Gets the full path of the given Eclipse Project Explorer resource (Project/File/Folder, etc).
     * 
     * @param resource - the resource.
     * @return the full path of the given resource.
     */
    public static String getFullPath(IResource resource) {
        return resource.getLocationURI().getPath().toString();
    }

    /// Conversion

    public static IFile toIfile(String fileAbsolutePath) {
        return getWorkspaceRoot().getFileForLocation(new Path(fileAbsolutePath));
    }

    ///~

    /// Get Eclipse resources
    private static IWorkbenchPage getActiveWorkbenchPage() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    }

    private static IWorkspaceRoot getWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    public static IViewPart getView(String id) {
        IViewReference[] viewReferences =
                getActiveWorkbenchPage().getViewReferences();
        for (int i = 0; i < viewReferences.length; i++) {
            if (id.equals(viewReferences[i].getId())) {
                return viewReferences[i].getView(false);
            }
        }
        return null;
    }

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
//        if (project != null && resourceName != null && project.exists() && project.isOpen()) {
//            try {
//                result = project.findType(resourceName);
//            } catch (JavaModelException e) {
//                String message = MessageFormat.format(
//                        "Cannot retrieve type ''{0}'' for project ''{1}''", resourceName, project.getElementName());
//                throw new DsmPluginException(message, e);
//            }
//        }
//        return result;
//    }

    public static IProject getProjectForResource(IResource resource) {
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
        IProject project = getWorkspaceRoot().getProject(name);
        if (project.exists()) {
            return project;
        }
        else {
            return null;
        }
    }

    /// ~

    public static void openInEditor(Dependable dependee) throws PartInitException, JavaModelException {

        String fullPath = DtanglerUtils.getAbsolutePath(dependee);
        IProject project = getProjectForResource(toIfile(fullPath));

        String binaryOutputLocation = getBinaryOutputLocation(project, false, false);

        // class can be located at Maven 'target' folder or Eclipse default 'bin' folder, or etc.
        if (fullPath.startsWith(binaryOutputLocation) && project.exists() && project.isOpen()) {

            StringBuilder sourceFileAbsolutePath = new StringBuilder();
            String projectFullPath = project.getLocationURI().getPath();
            sourceFileAbsolutePath.append(projectFullPath);

            String projectSourcesLocation = getSourcesLocation(project, false);
            sourceFileAbsolutePath.append(projectSourcesLocation);

            String classRelativePath = fullPath.substring(binaryOutputLocation.length());
            String javaSourceRelativePath = classRelativePath.replaceAll(".class", ".java");
            sourceFileAbsolutePath.append(javaSourceRelativePath);
            sourceFileAbsolutePath.toString();

            openInternalFileInAppropriateEditor(sourceFileAbsolutePath.toString());
        }

    }

    public static void openInternalFileInAppropriateEditor(String fileAbsolutePath) throws PartInitException {
        openInternalFileInAppropriateEditor(toIfile(fileAbsolutePath));
    }

    public static void openInternalFileInAppropriateEditor(IFile file) throws PartInitException {
        IWorkbenchPage page = getActiveWorkbenchPage();
        IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
        page.openEditor(new FileEditorInput(file), desc.getId());
    }

    public static void openExternalFileInAppropriateEditor(String fileAbsolutePath) throws PartInitException {
        openExternalFileInAppropriateEditor(toIfile(fileAbsolutePath));
    }

    public static void openExternalFileInAppropriateEditor(IFile fileToOpen) throws PartInitException {

        File file = fileToOpen.getFullPath().toFile();

        if (file.exists()) {
            throw new IllegalArgumentException("File does not exists: " + file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Is not a file: " + file.getAbsolutePath());
        }

        IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
        IWorkbenchPage page = getActiveWorkbenchPage();
        IDE.openEditorOnFileStore(page, fileStore);
    }

    public static void openExternalFileInDefaultTextEditor(String filePath) throws PartInitException {
        File file = new File(filePath);
        IFileStore fileOnLocalDisk = EFS.getLocalFileSystem().getStore(file.toURI());
        FileStoreEditorInput editorInput = new FileStoreEditorInput(fileOnLocalDisk);

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();

        page.openEditor(editorInput, EditorsUI.DEFAULT_TEXT_EDITOR_ID);
    }

    public static void revealInPackageExplorer(String fileAbsolutePath) {
        revealInPackageExplorer(new Path(fileAbsolutePath));
    }

    public static void revealInPackageExplorer(IPath fileAbsolutePath) {
        IWorkspace myWorkspace = ResourcesPlugin.getWorkspace();
        IFile file = myWorkspace.getRoot().getFileForLocation(fileAbsolutePath);
        revealInPackageExplorer(new StructuredSelection(file));
    }

    public static void revealInPackageExplorer(ISelection selection) {
        ((ProjectExplorer) getView(ProjectExplorer.VIEW_ID)).selectReveal(selection);
    }

    public static void showDsmView(IWorkbenchPart workbenchPart) throws PartInitException {
        workbenchPart.getSite().getPage().showView(DsmView.ID);
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

    public static String getSourcesLocation(IProject project, boolean includeProjectName) throws JavaModelException {
        IJavaProject javaProject = JavaCore.create(project);
        if (javaProject != null && javaProject.exists()) {
            for (IClasspathEntry classPathEntry : javaProject.getRawClasspath()) {
                if (classPathEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {

                    String sourcesLocation = classPathEntry.getPath().toOSString();
                    if (includeProjectName) {
                        return sourcesLocation;
                    } else {
                        String partToRemove = Path.SEPARATOR + javaProject.getElementName();
                        return sourcesLocation.substring(partToRemove.length());
                    }
                }
            }
            return null;
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
