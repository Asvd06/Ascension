package net.thejadeproject.ascension.refactor_packages.events.skills;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.clients.renderer.skills.VoidTraversalClientState;
import net.thejadeproject.ascension.clients.renderer.skills.VoidTraversalPostProcessor;
import org.joml.Matrix4f;

@EventBusSubscriber(
        modid = AscensionCraft.MOD_ID,
        value = Dist.CLIENT
)
public final class VoidTraversalClientEvents {

    private static final double TWO_PI = Math.PI * 2.0D;

    private static final int STAR_COUNT = 24;
    private static final int RING_COUNT = 3;

    private static boolean wasVoidTraversalActive = false;

    private VoidTraversalClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            VoidTraversalClientState.exit();
            wasVoidTraversalActive = false;
            VoidTraversalPostProcessor.close();
            return;
        }

        VoidTraversalClientState.clientTick();

        LocalPlayer player = minecraft.player;
        boolean active = VoidTraversalClientState.isActive();

        if (!active && wasVoidTraversalActive) {
            VoidTraversalPostProcessor.close();

            player.noPhysics = false;
            player.setNoGravity(false);
            player.fallDistance = 0.0F;
        }

        wasVoidTraversalActive = active;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (!VoidTraversalClientState.isActive()) return;

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null || minecraft.level == null) return;

        VoidTraversalPostProcessor.render(
                event.getPartialTick().getGameTimeDeltaPartialTick(false)
        );

        renderVoidSpace(event, player, VoidTraversalClientState.intensity());
    }

    @SuppressWarnings("rawtypes")
    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre event) {
        if (!VoidTraversalClientState.isActive()) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        Entity entity = event.getEntity();

        if (entity == minecraft.player) return;

        event.setCanceled(true);
    }

    private static void renderVoidSpace(
            RenderLevelStageEvent event,
            LocalPlayer player,
            float intensity
    ) {
        if (intensity <= 0.01F) return;

        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 cameraPosition = camera.getPosition();

        poseStack.pushPose();
        poseStack.translate(
                -cameraPosition.x,
                -cameraPosition.y,
                -cameraPosition.z
        );

        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        renderVoidStars(matrix, cameraPosition, player.tickCount, intensity);
        renderVoidRings(matrix, cameraPosition, player.tickCount, intensity);

        RenderSystem.lineWidth(1.0F);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    private static void renderVoidStars(
            Matrix4f matrix,
            Vec3 cameraPosition,
            int tickCount,
            float intensity
    ) {
        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.DEBUG_LINES,
                DefaultVertexFormat.POSITION_COLOR
        );

        double cx = cameraPosition.x;
        double cy = cameraPosition.y;
        double cz = cameraPosition.z;

        for (int i = 0; i < STAR_COUNT; i++) {
            double yaw = i * 2.399963D + tickCount * 0.00055D;
            double pitch = -0.9D + Math.floorMod(i * 37, 180) / 100.0D;
            double radius = 24.0D + Math.floorMod(i * 11, 22);

            double flat = Math.cos(pitch);

            Vec3 center = new Vec3(
                    cx + Math.cos(yaw) * flat * radius,
                    cy + Math.sin(pitch) * radius,
                    cz + Math.sin(yaw) * flat * radius
            );

            int pulse = Math.floorMod(tickCount + i * 13, 60);
            float twinkle = pulse < 30
                    ? pulse / 30.0F
                    : (60 - pulse) / 30.0F;

            int alpha = (int) ((45.0F + 95.0F * twinkle) * intensity);
            double size = 0.028D + (i % 4) * 0.01D;

            addLine(
                    buffer,
                    matrix,
                    center.add(-size, 0.0D, 0.0D),
                    center.add(size, 0.0D, 0.0D),
                    120,
                    145,
                    245,
                    alpha
            );

            addLine(
                    buffer,
                    matrix,
                    center.add(0.0D, -size, 0.0D),
                    center.add(0.0D, size, 0.0D),
                    120,
                    145,
                    245,
                    alpha
            );
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void renderVoidRings(
            Matrix4f matrix,
            Vec3 cameraPosition,
            int tickCount,
            float intensity
    ) {
        RenderSystem.lineWidth(1.8F);

        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.DEBUG_LINES,
                DefaultVertexFormat.POSITION_COLOR
        );

        double cx = cameraPosition.x;
        double cy = cameraPosition.y;
        double cz = cameraPosition.z;

        for (int i = 0; i < RING_COUNT; i++) {
            double yaw = i * TWO_PI / RING_COUNT + tickCount * 0.0016D;
            double distance = 6.0D + i * 3.4D;

            Vec3 center = new Vec3(
                    cx + Math.cos(yaw) * distance,
                    cy - 1.15D + Math.sin((tickCount + i * 23) * 0.018D) * 0.25D,
                    cz + Math.sin(yaw) * distance
            );

            double radius = 0.42D + (i % 3) * 0.18D;
            int segments = 28;

            int alpha = (int) ((70.0F + i * 8.0F) * intensity);

            for (int segment = 0; segment < segments; segment++) {
                if ((segment + i) % 5 == 0) continue;

                double a0 = segment * TWO_PI / segments + tickCount * 0.01D;
                double a1 = (segment + 1) * TWO_PI / segments + tickCount * 0.01D;

                Vec3 start = center.add(
                        Math.cos(a0) * radius,
                        0.0D,
                        Math.sin(a0) * radius
                );

                Vec3 end = center.add(
                        Math.cos(a1) * radius,
                        0.0D,
                        Math.sin(a1) * radius
                );

                addLine(
                        buffer,
                        matrix,
                        start,
                        end,
                        58,
                        42,
                        140,
                        alpha
                );
            }
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.lineWidth(1.0F);
    }

    private static void addLine(
            BufferBuilder buffer,
            Matrix4f matrix,
            Vec3 start,
            Vec3 end,
            int r,
            int g,
            int b,
            int a
    ) {
        if (a <= 0) return;

        buffer.addVertex(matrix, (float) start.x, (float) start.y, (float) start.z)
                .setColor(r, g, b, a);

        buffer.addVertex(matrix, (float) end.x, (float) end.y, (float) end.z)
                .setColor(r, g, b, a);
    }
}