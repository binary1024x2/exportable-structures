package xyz.binarydev.exportablestructures.exportablestructures.mixin;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructureBlockBlockEntity.class)
public interface StructureBlockBlockEntityAccessor {

    @Accessor
    String getAuthor();

}
