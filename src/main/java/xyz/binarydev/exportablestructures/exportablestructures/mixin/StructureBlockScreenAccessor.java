package xyz.binarydev.exportablestructures.exportablestructures.mixin;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.BlockMirror;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructureBlockScreen.class)
public interface StructureBlockScreenAccessor {

    @Accessor
    StructureBlockBlockEntity getStructureBlock();

    @Accessor
    CyclingButtonWidget<BlockMirror> getButtonMirror();

    @Accessor
    ButtonWidget getButtonRotate0();

    @Accessor
    ButtonWidget getButtonRotate90();

    @Accessor
    ButtonWidget getButtonRotate180();

    @Accessor
    ButtonWidget getButtonRotate270();

    @Accessor
    TextFieldWidget getInputName();

}
