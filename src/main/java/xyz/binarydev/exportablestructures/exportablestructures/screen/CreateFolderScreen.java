package xyz.binarydev.exportablestructures.exportablestructures.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.io.File;

public class CreateFolderScreen extends Screen {

    private TextFieldWidget nameWidget;
    private final ImportExportStructureScreen parent;
    private final File targetPath;

    public CreateFolderScreen(File targetPath, ImportExportStructureScreen parent) {
        super(Text.translatable("exportable_structures.button.create_folder"));
        this.parent = parent;
        this.targetPath = targetPath;
    }

    @Override
    public void init() {
        super.init();
        if (this.client == null) {
            return;
        }
        this.nameWidget = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, this.height / 2 - 22, 200, 20, this.nameWidget, Text.literal(""));
        addSelectableChild(nameWidget);
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("exportable_structures.button.create"), (button) -> {
            if (targetPath.isDirectory() && !this.nameWidget.getText().isEmpty()) {
                File newDir = new File(targetPath, this.nameWidget.getText());
                if (newDir.mkdir()) {
                    this.client.setScreen(this.parent);
                }
            }
        }).dimensions(this.width / 2 - 100, this.height / 2, 95, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("exportable_structures.button.cancel"), (button) -> {
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 + 5, this.height / 2, 95, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.nameWidget.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 45, 16777215);
    }

}
