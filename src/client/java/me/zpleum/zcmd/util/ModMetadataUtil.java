package me.zpleum.zcmd.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.Optional;
import java.util.stream.Collectors;

public class ModMetadataUtil {

    private static final String MOD_ID = "zcmd";

    private static ModMetadata metadata() {
        Optional<ModContainer> container =
                FabricLoader.getInstance().getModContainer(MOD_ID);
        return container.map(ModContainer::getMetadata).orElse(null);
    }

    public static String modName() {
        ModMetadata m = metadata();
        return m != null ? m.getName() : "Unknown Mod";
    }

    public static String authors() {
        ModMetadata m = metadata();
        if (m == null) return "Unknown";

        return m.getAuthors()
                .stream()
                .map(p -> p.getName())
                .collect(Collectors.joining(", "));
    }

    public static String license() {
        ModMetadata m = metadata();
        return (m != null && !m.getLicense().isEmpty())
                ? String.join(", ", m.getLicense())
                : "Under MIT License";
    }

    public static String version() {
        ModMetadata m = metadata();
        return m != null ? m.getVersion().getFriendlyString() : "?";
    }
}
