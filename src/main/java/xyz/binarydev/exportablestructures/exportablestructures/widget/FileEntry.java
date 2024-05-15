package xyz.binarydev.exportablestructures.exportablestructures.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import xyz.binarydev.exportablestructures.exportablestructures.screen.ExportStructureScreen;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileEntry extends AlwaysSelectedEntryListWidget.Entry<FileEntry> {

    private final MinecraftClient client;
    private final ExportStructureScreen screen;
    private final FileListWidget owner;

    private final File file;

    public FileEntry(FileListWidget fileList, FileListWidget files, File file) {
        this.file = file;
        this.owner = fileList;
        this.client = MinecraftClient.getInstance();
        this.screen = files.getParent();
    }

    public File getFile() {
        return file;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result = super.mouseClicked(mouseX, mouseY, button);
        owner.setSelected(this);
        screen.updateButtons(this);
        return result;
    }

    @Override
    public Text getNarration() {
        return Text.literal(file.getName());
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        context.drawText(this.client.textRenderer, file.getName().isEmpty() ? file.getPath() : file.getName(), x + 3, y + 1, 16777215, false);
        context.drawText(this.client.textRenderer, SimpleDateFormat.getDateTimeInstance().format(new Date(file.lastModified())), x + 3, y + 9 + 3, -8355712, false);
        context.drawText(this.client.textRenderer, (file.isFile() ? "File" : "Folder"), x + 3, y + 9 + 9 + 3, -8355712, false);
    }


}
