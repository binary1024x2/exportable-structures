package xyz.binarydev.exportablestructures.exportablestructures.mixin;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructureBlockScreen.class)
public interface StructureBlockScreenAccessor {

    @Accessor
    StructureBlockBlockEntity getStructureBlock();

}
