package xyz.binarydev.exportablestructures.exportablestructures;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.Identifier;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

public class FileStructureTemplateManager {

    private final StructureTemplateManager manager;
    private final LinkedHashMap<Identifier, File> loadedStructures;

    public FileStructureTemplateManager(StructureTemplateManager manager) {
        this.manager = manager;
        loadedStructures = new LinkedHashMap<>();
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
                loadedStructures.put(id, file);
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

    public Optional<StructureTemplate> loadFromFile(File file) {
        String md5 = getPathHash(file);
        Identifier id = Identifier.of("exportable-structures", md5);
        loadedStructures.putIfAbsent(id, file);
        return loadStructure(id);
    }

    public Optional<StructureTemplate> loadFromFile(String file) {
        return loadFromFile(new File(file));
    }

    public Optional<StructureTemplate> loadFromFile(URI file) throws MalformedURLException {
        return loadFromFile(file.toURL().getFile());
    }

    private Optional<StructureTemplate> loadStructure(Identifier id) {
        if (loadedStructures.containsKey(id)) {
            try(FileInputStream stream = new FileInputStream(loadedStructures.get(id))) {
                Optional<StructureTemplate> optional;
                try (FixedBufferInputStream inputStream = new FixedBufferInputStream(stream)) {
                    optional = Optional.of(this.readTemplate(inputStream));
                }
                return optional;
            } catch (IOException ex) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private StructureTemplate readTemplate(InputStream stream) throws IOException {
        NbtCompound compound = NbtIo.readCompressed(stream, NbtSizeTracker.ofUnlimitedBytes());
        return this.createTemplate(compound);
    }

    private StructureTemplate createTemplate(NbtCompound nbt) {
        StructureTemplate template = new StructureTemplate();
        int ver = NbtHelper.getDataVersion(nbt, 500);
        template.readNbt(manager.blockLookup, DataFixTypes.STRUCTURE.update(manager.dataFixer, nbt, ver));
        return template;
    }

    private String getPathHash(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(file.toString().getBytes(StandardCharsets.UTF_8));
            byte[] hash = digest.digest();
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(b);
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            return UUID.randomUUID().toString().replace('-', '_');
        }
    }
}
