package xyz.binarydev.exportablestructures.exportablestructures.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import org.apache.commons.io.FilenameUtils;
import xyz.binarydev.exportablestructures.exportablestructures.screen.ImportExportStructureScreen;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class FileListWidget extends AlwaysSelectedEntryListWidget<FileEntry> {

    private final ImportExportStructureScreen parent;
    private CompletableFuture<List<File>> filesFuture;
    //private List<File> files;
    private String path;
    private boolean showFiles = false;
    @SuppressWarnings("FieldMayBeFinal")
    private List<String> allowedExtensions = new ArrayList<>();

    public FileListWidget(ImportExportStructureScreen parent, MinecraftClient client, int width, int height, int y,
                          int itemHeight, String path, FileListWidget oldWidget) {
        super(client, width, height, y, itemHeight);
        this.parent = parent;
        this.path = path;
        if (oldWidget != null) {
            this.filesFuture = oldWidget.filesFuture;
        } else {
            this.filesFuture = loadFiles();
        }
        show(tryGet());
    }

    public void displayFiles(String... extensions) {
        showFiles = true;
        allowedExtensions.clear();
        allowedExtensions.addAll(Arrays.asList(extensions));
        show(tryGet());
    }

    public void hideFiles() {
        showFiles = false;
        show(tryGet());
    }

    public ImportExportStructureScreen getParent() {
        return parent;
    }

    public void setPath(String path) {
        this.path = path;
        showLoadingScreen();
        this.filesFuture = loadFiles();
        show(tryGet());
    }

    private void show(List<File> files) {
        if (files == null) {
            this.showLoadingScreen();
        } else {
            this.showFiles(files);
        }
        //this.files = files;
    }

    private List<File> tryGet() {
        try {
            return this.loadFiles().getNow(Arrays.asList(File.listRoots()));
        } catch (CancellationException | CompletionException var2) {
            return null;
        }
    }

    private CompletableFuture<List<File>> loadFiles() {
        List<File> files = new ArrayList<>(path == null ? Arrays.asList(File.listRoots()) : Arrays.stream(Objects.requireNonNull(new File(path).listFiles())).filter(x -> {
            if (showFiles) {
                if (x.isFile()) {
                    String ext = FilenameUtils.getExtension(x.getName());
                    if (allowedExtensions.isEmpty() || allowedExtensions.contains(ext)) {
                        return !x.isHidden();
                    } else {
                        return false;
                    }
                }
                return !x.isHidden();
            }
            return x.isDirectory() && !x.isHidden();
        }).sorted((file1, file2) -> {
            if ((file1.isFile() && file2.isFile()) || (file1.isDirectory() && file2.isDirectory())) {
                return file1.getName().compareToIgnoreCase(file2.getName());
            } else if (file1.isDirectory() && file2.isFile()) {
                return -1;
            } else if (file1.isFile() && file2.isDirectory()) {
                return 1;
            } else {
                return 0;
            }
        }).toList());
        return CompletableFuture.completedFuture(files);
    }

    private void showLoadingScreen() {
        this.clearEntries();
        parent.updateButtons(null);
    }

    private void showFiles(List<File> files) {
        this.clearEntries();
        for (File file : files) {
            this.addEntry(new FileEntry(this, this, file));
        }
        //this.narrateScreenIfNarrationEnabled();
    }

}
