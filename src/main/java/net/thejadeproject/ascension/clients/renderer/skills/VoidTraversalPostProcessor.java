package net.thejadeproject.ascension.clients.renderer.skills;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.AscensionCraft;

public final class VoidTraversalPostProcessor {

    private static PostChain chain;
    private static int lastWidth = -1;
    private static int lastHeight = -1;
    private static boolean failed = false;

    private VoidTraversalPostProcessor() {
    }

    public static void render(float partialTick) {
        if (failed) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;

        try {
            ensureChain(minecraft);

            if (chain == null) return;

            chain.process(partialTick);
            minecraft.getMainRenderTarget().bindWrite(false);
        } catch (Exception e) {
            failed = true;
            AscensionCraft.LOGGER.error("Failed to render Void Traversal post process", e);
        }
    }

    public static void close() {
        if (chain != null) {
            chain.close();
            chain = null;
        }

        lastWidth = -1;
        lastHeight = -1;
        failed = false;
    }

    private static void ensureChain(Minecraft minecraft) throws Exception {
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        if (chain != null && width == lastWidth && height == lastHeight) {
            return;
        }

        close();

        chain = new PostChain(
                minecraft.getTextureManager(),
                minecraft.getResourceManager(),
                minecraft.getMainRenderTarget(),
                ResourceLocation.fromNamespaceAndPath(
                        AscensionCraft.MOD_ID,
                        "shaders/post/void_traversal.json"
                )
        );

        chain.resize(width, height);

        lastWidth = width;
        lastHeight = height;
    }
}