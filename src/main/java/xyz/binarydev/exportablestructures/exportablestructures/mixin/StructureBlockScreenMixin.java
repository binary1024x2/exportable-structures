package xyz.binarydev.exportablestructures.exportablestructures.mixin;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.binarydev.exportablestructures.exportablestructures.screen.ImportExportStructureScreen;

@Mixin(StructureBlockScreen.class)
public class StructureBlockScreenMixin {

    @Shadow @Final private StructureBlockBlockEntity structureBlock;
    @Unique
    private ButtonWidget exportButton;
    @Unique
    private ButtonWidget importButton;
    @Unique
    private CyclingButtonWidget<BlockRotation> rotationButton;

    @Inject(method = "init()V", at = @At("HEAD"))
    private void init(CallbackInfo info) {
        StructureBlockScreen self = (StructureBlockScreen) ((Object) this);
        StructureBlockScreenAccessor accessor = (StructureBlockScreenAccessor) self;
        this.exportButton = ((ScreenInvoker) self).invokeAddDrawableChild(ButtonWidget.builder(Text.translatable("exportable_structures.button.export"), (button) -> {
            if (accessor.getStructureBlock().getMode() == StructureBlockMode.SAVE) {
                MinecraftClient.getInstance().setScreen(new ImportExportStructureScreen(self, accessor.getStructureBlock(), ImportExportStructureScreen.MODE_SAVE).hideFiles());
            }
        }).dimensions(self.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 50, 20).build());
        this.importButton = ((ScreenInvoker) self).invokeAddDrawableChild(ButtonWidget.builder(Text.translatable("exportable_structures.button.import"), (button) -> {
            if (accessor.getStructureBlock().getMode() == StructureBlockMode.LOAD) {
                MinecraftClient.getInstance().setScreen(new ImportExportStructureScreen(self, accessor.getStructureBlock(), ImportExportStructureScreen.MODE_LOAD).showFiles("nbt").setOnFileSelected((screen, file) -> {
                    screen.close();
                    accessor.getInputName().setText(file.toURI().toString());
                }));
            }
        }).dimensions(self.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 50, 20).build());
        this.rotationButton = ((ScreenInvoker) self).invokeAddDrawableChild(CyclingButtonWidget.<BlockRotation>builder((value) -> {
            switch (value) {
                case CLOCKWISE_90 -> {
                    return Text.literal("90");
                }
                case CLOCKWISE_180 -> {
                    return Text.literal("180");
                }
                case COUNTERCLOCKWISE_90 -> {
                    return Text.literal("270");
                }
                default -> {
                    return Text.literal("0");
                }
            }
        }).values(BlockRotation.values())
                .omitKeyText()
                .initially(structureBlock.getRotation())
                .build(self.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20, Text.literal("Rotation"), (button, value) -> structureBlock.setRotation(value)));
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void initTail(CallbackInfo info) {
        StructureBlockScreen self = (StructureBlockScreen) ((Object) this);
        StructureBlockScreenAccessor accessor = (StructureBlockScreenAccessor) self;
        accessor.getButtonMirror().setDimensionsAndPosition(40, 20, self.width / 2 + 1 + 20, 185);
        accessor.getInputName().setMaxLength(4096);
    }


    @Inject(method = "updateWidgets(Lnet/minecraft/block/enums/StructureBlockMode;)V", at = @At("TAIL"))
    private void updateWidgets(StructureBlockMode mode, CallbackInfo info) {
        StructureBlockScreen self = (StructureBlockScreen) ((Object) this);
        StructureBlockScreenAccessor accessor = (StructureBlockScreenAccessor) self;
        exportButton.visible = (mode == StructureBlockMode.SAVE);
        importButton.visible = (mode == StructureBlockMode.LOAD);
        rotationButton.visible = (mode == StructureBlockMode.LOAD);
        accessor.getButtonRotate0().visible = false;
        accessor.getButtonRotate90().visible = false;
        accessor.getButtonRotate180().visible = false;
        accessor.getButtonRotate270().visible = false;

    }

}
