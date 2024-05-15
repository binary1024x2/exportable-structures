package xyz.binarydev.exportablestructures.exportablestructures.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import xyz.binarydev.exportablestructures.exportablestructures.net.SaveStructureToFilePayload;
import xyz.binarydev.exportablestructures.exportablestructures.widget.FileEntry;
import xyz.binarydev.exportablestructures.exportablestructures.widget.FileListWidget;

import java.io.File;
import java.util.Objects;

public class ExportStructureScreen extends Screen {

    private TextFieldWidget pathWidget;
    private FileListWidget fileList;
    private final StructureBlockScreen parent;
    private File currentFile;
    private ButtonWidget createFolderButton;
    private ButtonWidget deleteButton;
    private ButtonWidget upButton;
    private ButtonWidget saveButton;
    private ButtonWidget openFolderButton;
    private final StructureBlockBlockEntity structureBlock;

    public ExportStructureScreen(StructureBlockScreen parent, StructureBlockBlockEntity structureBlock) {
        super(Text.translatable("exportable_structures.screen.export_structure"));
        this.parent = parent;
        this.structureBlock = structureBlock;
    }

    @Override
    public void init() {
        super.init();
        if (this.client == null) {
            return;
        }
        this.pathWidget = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.pathWidget, Text.literal(""));
        this.addSelectableChild(this.pathWidget);
        fileList = this.addDrawableChild(new FileListWidget(this, this.client, this.width, this.height - 112, 48, 36, currentFile == null ? null : currentFile.toString(), this.fileList));
        createFolderButton = addDrawableChild(ButtonWidget.builder(Text.translatable("exportable_structures.button.create_folder"), (button) -> {
            MinecraftClient.getInstance().setScreen(new CreateFolderScreen(currentFile, this));
        }).dimensions(this.width / 2 - 154, this.height - 28, 100, 20).build());
        deleteButton = addDrawableChild(ButtonWidget.builder(Text.translatable("exportable_structures.button.delete"), (button) -> {
            File selected = Objects.requireNonNull(fileList.getFocused()).getFile();
            MinecraftClient.getInstance().setScreen(new ConfirmDeleteFileScreen(selected, this));
        }).dimensions(this.width / 2 - 50, this.height - 52, 100, 20).build());
        upButton = addDrawableChild(ButtonWidget.builder(Text.translatable("exportable_structures.button.up"), (button) -> {
            if (currentFile != null && currentFile.isDirectory()) {
                if (currentFile.getParentFile() == null) {
                    fileList.setPath(null);
                    pathWidget.setText("");
                    currentFile = null;
                } else {
                    File parent = currentFile.getParentFile();
                    if (!parent.isFile()) {
                        fileList.setPath(parent.toString());
                        pathWidget.setText(parent.toString());
                        currentFile = parent;
                    }
                }
            }
            updateButtons(null);
        }).dimensions(this.width / 2 - 50, this.height - 28, 100, 20).build());
        saveButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("exportable_structures.button.save"), (button) -> {
            if (currentFile != null) {
                BlockPos pos = structureBlock.getPos();
                ClientPlayNetworking.send(new SaveStructureToFilePayload(currentFile.toString(), pos));
                this.client.setScreen(null);
            }
        }).dimensions(this.width / 2 + 54, this.height - 52, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("exportable_structures.button.cancel"), (button) -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 54, this.height - 28, 100, 20).build());
        openFolderButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("exportable_structures.button.open_folder"), (button) -> {
            File selected = Objects.requireNonNull(fileList.getFocused()).getFile();
            if (!selected.isFile()) {
                fileList.setPath(selected.toString());
                pathWidget.setText(selected.toString());
                currentFile = selected;
            }
            updateButtons(null);
        }).dimensions(this.width / 2 - 154, this.height - 52, 100, 20).build());
        updateButtons(null);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.pathWidget.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 16777215);
    }

    public void updateButtons(FileEntry entry) {
        createFolderButton.active = (currentFile != null);
        upButton.active = (currentFile != null);
        deleteButton.active = (entry != null && entry.getFile().getParentFile() != null);
        saveButton.active = (currentFile != null);
        openFolderButton.active = (entry != null);
    }

}
