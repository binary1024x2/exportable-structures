package xyz.binarydev.exportablestructures.exportablestructures.mixin;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.structure.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructureBlockBlockEntity.class)
public interface StructureBlockBlockEntityAccessor {

    @Accessor
    String getAuthor();

    @Invoker("loadStructure")
    void invokeLoadStructure(StructureTemplate template);

}
