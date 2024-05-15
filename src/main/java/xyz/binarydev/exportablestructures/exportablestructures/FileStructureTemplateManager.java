package xyz.binarydev.exportablestructures.exportablestructures;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class FileStructureTemplateManager {

    private final StructureTemplateManager manager;

    public FileStructureTemplateManager(StructureTemplateManager manager) {
        this.manager = manager;
    }

    public boolean saveToFile(File file, Identifier id) throws IOException {
        Optional<StructureTemplate> optional = manager.getTemplate(id);
        if (optional.isEmpty()) {
            return false;
        } else {
            StructureTemplate template = optional.get();
            File path = getPath(file, id);
            NbtCompound compound = template.writeNbt(new NbtCompound());
            try {
                OutputStream stream = new FileOutputStream(path);
                try {
                    NbtIo.writeCompressed(compound, stream);
                } catch (IOException ex) {
                    try {
                        stream.close();
                    } catch (IOException ignored) {}
                    throw ex;
                }
                stream.close();
                return true;
            } catch (IOException ex) {
                return false;
            }
        }
    }

    private File getPath(File parent, Identifier id) {
        String name = id.toUnderscoreSeparatedString();
        return new File(parent, name + ".nbt");
    }
}
