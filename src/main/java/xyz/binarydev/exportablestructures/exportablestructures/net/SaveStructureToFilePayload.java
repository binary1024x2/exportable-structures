package xyz.binarydev.exportablestructures.exportablestructures.net;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class SaveStructureToFilePayload implements CustomPayload {

    public static final Id<SaveStructureToFilePayload> ID = new CustomPayload.Id<>(Identifier.of("exportablestructures:save_structure_to_file"));
    public static final PacketCodec<PacketByteBuf, SaveStructureToFilePayload> CODEC = PacketCodec.of(SaveStructureToFilePayload::write, SaveStructureToFilePayload::new);

    private final String path;
    private final BlockPos pos;

    public SaveStructureToFilePayload(String path, BlockPos pos) {
        this.path = path;
        this.pos = pos;
    }

    private SaveStructureToFilePayload(PacketByteBuf buf) {
        this.path = buf.readString();
        this.pos = buf.readBlockPos();
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private void write(PacketByteBuf buf) {
        buf.writeString(this.path);
        buf.writeBlockPos(this.pos);
    }

    public String getPath() {
        return this.path;
    }

    public BlockPos getPos() {
        return this.pos;
    }

}
