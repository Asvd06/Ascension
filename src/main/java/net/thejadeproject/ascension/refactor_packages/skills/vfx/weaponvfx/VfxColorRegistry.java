package net.thejadeproject.ascension.refactor_packages.skills.vfx.weaponvfx;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class VfxColorRegistry {

    private static final Map<String, Map<ResourceLocation, String>> REGISTRY = new HashMap<>();

    private VfxColorRegistry() {}

    // ── Registration ─────────────────────────────────────────────────────────

    public static void register(String vfxType, ResourceLocation techniqueId, String colorFolder) {
        REGISTRY.computeIfAbsent(vfxType, k -> new HashMap<>()).put(techniqueId, colorFolder);
    }

    // ── Lookup ────────────────────────────────────────────────────────────────

    public static String resolve(String vfxType, ResourceLocation techniqueId, String fallback) {
        if (techniqueId == null) return fallback;
        Map<ResourceLocation, String> inner = REGISTRY.get(vfxType);
        if (inner == null) return fallback;
        return inner.getOrDefault(techniqueId, fallback);
    }

    public static String resolve(String vfxType, ResourceLocation techniqueId) {
        return resolve(vfxType, techniqueId, "blue");
    }

    public static String resolveTexturePath(String vfxType, ResourceLocation techniqueId, String fallbackColor) {
        return "entity/vfx/" + vfxType + "/" + resolve(vfxType, techniqueId, fallbackColor);
    }
}
