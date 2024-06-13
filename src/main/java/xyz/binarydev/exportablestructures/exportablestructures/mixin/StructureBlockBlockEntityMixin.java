package xyz.binarydev.exportablestructures.exportablestructures.mixin;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.binarydev.exportablestructures.exportablestructures.FileStructureTemplateManager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

@Mixin(StructureBlockBlockEntity.class)
public abstract class StructureBlockBlockEntityMixin {

    @Shadow private StructureBlockMode mode;
    @Shadow private Vec3i size;

    @Shadow protected abstract void loadAndPlaceStructure(ServerWorld world, StructureTemplate template);

    @Unique @Nullable
    private String templateFile = null;

    @Inject(method = "setTemplateName(Ljava/lang/String;)V", at = @At("HEAD"))
    public void setTemplateName(String templateName, CallbackInfo info) {
        StructureBlockBlockEntity self = (StructureBlockBlockEntity) (Object) this;
        if (StringHelper.isEmpty(templateName)) {
            self.setTemplateName((Identifier) null);
        } else if (templateName.startsWith("file:")) {
            templateFile = templateName;
        } else {
            self.setTemplateName(Identifier.tryParse(templateName));
        }
    }

    @Inject(method = "hasStructureName()Z", at = @At("RETURN"), cancellable = true)
    public void hasStructureName(CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(info.getReturnValue() || templateFile != null);
    }

    @Inject(method = "getTemplateName()Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
    public void getTemplateName(CallbackInfoReturnable<String> info) {
        if (info.getReturnValue() == null || info.getReturnValue().isEmpty()) {
            if (templateFile != null) {
                info.setReturnValue(templateFile);
            }
        }
    }

    @Inject(method = "isStructureAvailable()Z", at = @At("HEAD"), cancellable = true)
    public void isStructureAvailable(CallbackInfoReturnable<Boolean> info) {
        StructureBlockBlockEntity self = (StructureBlockBlockEntity) (Object) this;
        if (self.getMode() != StructureBlockMode.LOAD || (self.getWorld() != null && !self.getWorld().isClient)) {
            if (this.templateFile != null) {
                try {
                    File f = new File(URI.create(templateFile).toURL().getFile());
                    info.setReturnValue(f.exists() && f.isFile());
                } catch (IOException ex) {
                    info.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "loadAndTryPlaceStructure(Lnet/minecraft/server/world/ServerWorld;)Z", at = @At("RETURN"), cancellable = true)
    public void loadAndTryPlaceStructure(ServerWorld world, CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue()) {
            if (this.mode == StructureBlockMode.LOAD && this.templateFile != null) {
                try {
                    FileStructureTemplateManager manager = new FileStructureTemplateManager(world.getStructureTemplateManager());
                    StructureTemplate structure = manager.loadFromFile(URI.create(this.templateFile)).orElse(null);
                    if (structure == null) {
                        info.setReturnValue(false);
                        return;
                    }
                    if (structure.getSize().equals(size)) {
                        loadAndPlaceStructure(world, structure);
                        info.setReturnValue(true);
                        return;
                    }
                    StructureBlockBlockEntityAccessor accessor = (StructureBlockBlockEntityAccessor) this;
                    accessor.invokeLoadStructure(structure);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
