package xyz.binarydev.exportablestructures.exportablestructures.mixin;

import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.binarydev.exportablestructures.exportablestructures.screen.ExportStructureScreen;

@Mixin(StructureBlockScreen.class)
public class StructureBlockScreenMixin {

    @Unique
    private ButtonWidget exportButton;

    @Inject(method = "init()V", at = @At("HEAD"))
    private void init(CallbackInfo info) {
        StructureBlockScreen self = (StructureBlockScreen) ((Object) this);
        StructureBlockScreenAccessor accessor = (StructureBlockScreenAccessor) self;
        this.exportButton = ((ScreenInvoker) self).invokeAddDrawableChild(ButtonWidget.builder(Text.translatable("exportable_structures.button.export"), (button) -> {
            if (accessor.getStructureBlock().getMode() == StructureBlockMode.SAVE) {
                MinecraftClient.getInstance().setScreen(new ExportStructureScreen(self, accessor.getStructureBlock()));
            }
        }).dimensions(self.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 50, 20).build());
    }

    @Inject(method = "updateWidgets(Lnet/minecraft/block/enums/StructureBlockMode;)V", at = @At("TAIL"))
    private void updateWidgets(StructureBlockMode mode, CallbackInfo info) {
        exportButton.visible = (mode == StructureBlockMode.SAVE);
    }

}
