package com.dsmviewer.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;

import org.dtangler.core.dependencies.Dependable;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
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
import com.dsmviewer.PluginException;
import com.dsmviewer.dsm.DependencyScope;
import com.dsmviewer.logging.Logger;
import com.dsmviewer.ui.DsmView;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public final class EclipseUtils {

    private static final Logger LOG = Activator.getLogger(EclipseUtils.class);

    /// ~

    public static void openInJavaEditor(Dependable javaClassResource) {
        String sourceFileAbsolutePath = getSourceFilePath(javaClassResource);
        if (sourceFileAbsolutePath != null) {
            openInternalFileInAppropriateEditor(sourceFileAbsolutePath);
        }
    }

    public static IFile getSourceFile(Dependable javaClassResource) {
        String sourceFilePath = getSourceFilePath(javaClassResource);
        if (sourceFilePath != null) {
            return toIfile(sourceFilePath);
        } else {
            throw new IllegalArgumentException("Cannot find appropriate Eclipse resource for: " + javaClassResource);
        }
    }

    public static String getSourceFilePath(Dependable javaClassResource) {
        StringBuilder sourceFileAbsolutePath = new StringBuilder();

        String javaClassResourceFullPath = DtanglerUtils.getAbsolutePath(javaClassResource, DependencyScope.CLASSES);
        IProject project = getOpenedProjectForResource(toIfile(javaClassResourceFullPath));

        // Compiled classes can be located anywhere (Maven 'target' folder, Eclipse default 'bin' folder, etc)
        String binaryOutputLocation = getBinaryOutputLocation(project, false, false);

        if (javaClassResourceFullPath.startsWith(binaryOutputLocation) && project.exists() && project.isOpen()) {

            String projectFullPath = project.getLocationURI().getPath();
            sourceFileAbsolutePath.append(projectFullPath);

            String projectSourcesLocation = getSourcesLocation(project, false);
            sourceFileAbsolutePath.append(projectSourcesLocation);

            String javaClassRelativePath = javaClassResourceFullPath.substring(binaryOutputLocation.length());
            String javaResourceRelativePath = javaClassRelativePath.replaceAll(".class", ".java");
            sourceFileAbsolutePath.append(javaResourceRelativePath);
            sourceFileAbsolutePath.toString();
            return sourceFileAbsolutePath.toString();

        }
        return null;
    }

    public static void openInternalFileInAppropriateEditor(String fileAbsolutePath) {
        openInternalFileInAppropriateEditor(toIfile(fileAbsolutePath));
    }

    public static void openInternalFileInAppropriateEditor(IFile file) {
        try {
            IWorkbenchPage page = getActiveWorkbenchPage();
            IEditorRegistry editorRegistry = page.getWorkbenchWindow().getWorkbench().getEditorRegistry();
            IEditorDescriptor desc = editorRegistry.getDefaultEditor(file.getName());
            page.openEditor(new FileEditorInput(file), desc.getId());
        } catch (PartInitException e) {
            LOG.error("Cannot open resource in editor: " + file.getFullPath(), e);
        }
    }

    public static void openExternalFileInAppropriateEditor(String fileAbsolutePath) {
        openExternalFileInAppropriateEditor(toIfile(fileAbsolutePath));
    }

    public static void openExternalFileInAppropriateEditor(IFile fileToOpen) {
        try {
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

        } catch (PartInitException e) {
            LOG.error("Cannot open resource in editor: " + fileToOpen.getFullPath(), e);
        }
    }

    public static void openExternalFileInDefaultTextEditor(String filePath) {
        try {
            File file = new File(filePath);
            IFileStore fileOnLocalDisk = EFS.getLocalFileSystem().getStore(file.toURI());
            FileStoreEditorInput editorInput = new FileStoreEditorInput(fileOnLocalDisk);

            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();

            page.openEditor(editorInput, EditorsUI.DEFAULT_TEXT_EDITOR_ID);
        } catch (PartInitException e) {
            LOG.error("Cannot open resource in default text editor: " + filePath, e);
        }
    }

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

    /**
     * Gets an absolute path from resource relative path.
     * 
     * @param filePath - relative path to any resource.
     * @return absolute path to given resource
     */
    public static String getAbsolutePath(final String filePath) {
        String result = null;
        URL fileUrl = Activator.getInstance().getBundle().getEntry(filePath);
        try {
            result = FileLocator.toFileURL(fileUrl).getFile();
        } catch (IOException e) {
            LOG.error("Cannot retrieve absolute path for the file: " + filePath, e);
        }
        return result;
    }

    public static InputStream getResourceAsStream(String resourcePath) {
        InputStream openStream = null;
        try {
            openStream = FileLocator.openStream(Activator.getInstance().getBundle(), new Path(resourcePath), false);
        } catch (IOException e) {
            LOG.error("Cannot retrieve input stream for resource: " + resourcePath, e);
        }
        return openStream;
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
        IViewReference[] viewReferences = getActiveWorkbenchPage().getViewReferences();
        for (int i = 0; i < viewReferences.length; i++) {
            if (id.equals(viewReferences[i].getId())) {
                return viewReferences[i].getView(false);
            }
        }
        return null;
    }

    public static IProject getOpenedProjectForResource(IResource resource) {
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
    public static IProject getOpenedWorkspaceProject(String name) {
        IProject project = getWorkspaceRoot().getProject(name);
        if (project.exists() && project.isOpen()) {
            return project;
        }
        else {
            return null;
        }
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

    public static void showDsmView() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage activeWorkbenchPage = workbenchWindow.getActivePage();
        try {
            // open view in background
            activeWorkbenchPage.showView(DsmView.ID);
        } catch (PartInitException e) {
            LOG.error("Cannot show Dsm View (id=" + DsmView.ID + ")", e);
        }
        // and ensure that it is bringed on top
        activeWorkbenchPage.activate(DsmView.getCurrent());
        DsmView.getCurrent().setFocus();
    }

    public static void hideDsmView() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
        workbenchWindow.getActivePage().hideView(getView(DsmView.ID));
    }

    public static void showDsmView(IWorkbenchPart workbenchPart) throws PartInitException {
        workbenchPart.getSite().getPage().showView(DsmView.ID);
    }

    /**
     * Gets the 'OutputLocation' path for project is opened in workspace
     */
    public static String getBinaryOutputLocation(String projectName, boolean relativePath, boolean includeProjectName) {
        IProject openedProject = getOpenedWorkspaceProject(projectName);
        return getBinaryOutputLocation(openedProject, relativePath, includeProjectName);
    }

    /**
     * Gets the binaries output location for given project
     */
    public static String getBinaryOutputLocation(IProject project, boolean relativePath, boolean includeProjectName) {
        IJavaProject javaProject = JavaCore.create(project);
        if (javaProject.exists()) {
            return getBinaryOutputLocation(javaProject, relativePath, includeProjectName);
        } else {
            LOG.debug("Cannot find binaries output location for Java project: " + project.getName());
        }
        return null;
    }

    public static IJavaProject toIJavaProject(IProject project) {
        IJavaProject javaProject = JavaCore.create(project);
        if (javaProject.exists()) {
            return javaProject;
        } else {
            LOG.debug("Cannot find appropriate Java project in Eclipse workspace for: " + project.getName());
            return null;
        }
    }

    /**
     * Gets the sources location for given project
     */
    public static String getSourcesLocation(IProject project, boolean includeProjectName) {
        try {
            return internalGetSourcesLocation(project, includeProjectName);
        } catch (JavaModelException e) {
            LOG.error("Cannot find sources location for Java project: " + project.getName(), e);
        }
        return null;
    }

    private static String internalGetSourcesLocation(IProject project, boolean includeProjectName)
            throws JavaModelException {
        IJavaProject javaProject = JavaCore.create(project);
        if (javaProject.exists()) {
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
        }
        return null;
    }

    public static String getBinaryOutputLocation(IJavaProject javaProject, boolean relativePath,
            boolean includeProjectName) {

        String result = null;

        try {
            result = javaProject.getOutputLocation().toOSString();
            if (!includeProjectName) {
                result = result.replaceFirst(File.separator + javaProject.getElementName(), "");
            }
            if (!relativePath) {
                result = javaProject.getCorrespondingResource().getLocationURI().getRawPath().concat(result);
            }
        } catch (JavaModelException e) {
            String message = MessageFormat.format(
                    "Cannot retrieve binary output location for project ''{0}''", javaProject.getElementName());
            LOG.error(message, e);
            throw new PluginException(message, e);
        }

        return result;
    }

    public static String readIFileAsString(IFile file) {
        String result = null;

        try {
            InputStream is = null;
            try {
                is = readIFileAsInputStream(file);
                result = Utils.convertStreamToString(is);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (IOException e) {
            LOG.error(MessageFormat.format("Cannot read file ''{0}'' as string", file), e);
        }

        return result;
    }

    public static InputStream readIFileAsInputStream(IFile file) {
        InputStream is = null;

        try {
            is = file.getContents();
        } catch (CoreException e) {
            e.printStackTrace();
        }

        return is;
    }

//    public static void visitAllResources(IProject project) {
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
