package xyz.binarydev.exportablestructures.exportablestructures;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.binarydev.exportablestructures.exportablestructures.mixin.StructureBlockBlockEntityAccessor;
import xyz.binarydev.exportablestructures.exportablestructures.net.SaveStructureToFilePayload;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ExportableStructures implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("exportable-structures");

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(SaveStructureToFilePayload.ID, SaveStructureToFilePayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SaveStructureToFilePayload.ID, (payload, context) -> {
            ServerWorld world = context.player().getServerWorld();
            MinecraftServer server = world.getServer();
            BlockPos blockPos = payload.getPos();
            StructureBlockBlockEntity structureBlock = (StructureBlockBlockEntity) world.getBlockEntity(blockPos);
            if (structureBlock == null) {
                context.player().sendMessage(Text.translatable("exportable_structures.message.failed_to_get_info"), false);
                return;
            }
            if (Objects.equals(structureBlock.getTemplateName(), "") || structureBlock.getTemplateName() == null) {
                context.player().sendMessage(Text.translatable("exportable_structures.message.unable_to_export"), false);
                return;
            }
            BlockPos pos = structureBlock.getPos().add(structureBlock.getOffset());
            StructureTemplateManager manager = server.getStructureTemplateManager();
            StructureTemplate template;
            try {
                template = manager.getTemplateOrBlank(Identifier.of(structureBlock.getTemplateName()));
            } catch (InvalidIdentifierException ex) {
                LOGGER.warn("Failed to create template", ex);
                context.player().sendMessage(Text.translatable("exportable_structures.message.unable_to_export"), false);
                return;
            }
            template.saveFromWorld(structureBlock.getWorld(), pos, structureBlock.getSize(), !structureBlock.shouldIgnoreEntities(), Blocks.STRUCTURE_VOID);
            StructureBlockBlockEntityAccessor accessor = (StructureBlockBlockEntityAccessor)structureBlock;
            template.setAuthor(accessor.getAuthor());
            try {
                FileStructureTemplateManager fileManager = new FileStructureTemplateManager(manager);
                File target = new File(payload.getPath());
                if (fileManager.saveToFile(target, Identifier.of(structureBlock.getTemplateName()))) {
                    context.player().sendMessage(Text.translatable("exportable_structures.message.saved_structure_file", structureBlock.getTemplateName(), target.toString()), false);
                } else {
                    context.player().sendMessage(Text.translatable("exportable_structures.message.failed_save_structure_file", structureBlock.getTemplateName(), target.toString()), false);
                }
            } catch (IOException ex) {
                LOGGER.warn("Failed to save template", ex);
                context.player().sendMessage(Text.translatable("exportable_structures.message.unable_to_export"), false);
            }
        });
    }
}
