package xyz.binarydev.exportablestructures.exportablestructures.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.io.File;

public class ConfirmDeleteFileScreen extends Screen {

    private final File file;
    private final ExportStructureScreen parent;

    public ConfirmDeleteFileScreen(File file, ExportStructureScreen parent) {
        super(Text.translatable("exportable_structures.button.delete"));
        this.file = file;
        this.parent = parent;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void init() {
        super.init();
        if (this.client == null) {
            return;
        }
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("exportable_structures.button.delete"), (button) -> {
            if (file.isFile()) {
                file.delete();
            } else {
                deleteRecursive(file);
            }
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 100, this.height / 2, 95, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("exportable_structures.button.cancel"), (button) -> {
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 + 5, this.height / 2, 95, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 45, 16777215);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("exportable_structures.message.confirm_delete", file.getName()), this.width / 2, this.height / 2 - 20, 16777215);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteRecursive(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteRecursive(f);
            }
        }
        file.delete();
    }
}
