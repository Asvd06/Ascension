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
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.clients.renderer.skills.VoidTraversalClientState;
import org.joml.Matrix4f;

@EventBusSubscriber(
        modid = AscensionCraft.MOD_ID,
        value = Dist.CLIENT
)
public final class VoidTraversalClientEvents {

    private static final double TWO_PI = Math.PI * 2.0D;

    private static final int STAR_COUNT = 42;
    private static final int SEAM_COUNT = 18;
    private static final int RING_COUNT = 8;

    private VoidTraversalClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            VoidTraversalClientState.exit();
            return;
        }

        VoidTraversalClientState.clientTick();
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (!VoidTraversalClientState.isActive()) return;

        float intensity = VoidTraversalClientState.intensity();

        event.setNearPlaneDistance(0.0F);
        event.setFarPlaneDistance(1.5F + 2.5F * (1.0F - intensity));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (!VoidTraversalClientState.isActive()) return;

        event.setRed(0.0F);
        event.setGreen(0.0F);
        event.setBlue(0.0F);
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

        renderBlackVoidBox(matrix, cameraPosition, intensity);
        renderVoidStars(matrix, cameraPosition, player.tickCount, intensity);
        renderVoidSeams(matrix, cameraPosition, player.tickCount, intensity);
        renderVoidRings(matrix, cameraPosition, player.tickCount, intensity);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    private static void renderBlackVoidBox(
            Matrix4f matrix,
            Vec3 cameraPosition,
            float intensity
    ) {
        double size = 96.0D;
        double x = cameraPosition.x;
        double y = cameraPosition.y;
        double z = cameraPosition.z;

        int alpha = (int) (252.0F * intensity);

        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_COLOR
        );

        // Bottom
        addQuad(buffer, matrix,
                x - size, y - size, z - size,
                x + size, y - size, z - size,
                x + size, y - size, z + size,
                x - size, y - size, z + size,
                0, 0, 5, alpha
        );

        // Top
        addQuad(buffer, matrix,
                x - size, y + size, z + size,
                x + size, y + size, z + size,
                x + size, y + size, z - size,
                x - size, y + size, z - size,
                0, 0, 5, alpha
        );

        // North
        addQuad(buffer, matrix,
                x - size, y - size, z - size,
                x - size, y + size, z - size,
                x + size, y + size, z - size,
                x + size, y - size, z - size,
                0, 0, 5, alpha
        );

        // South
        addQuad(buffer, matrix,
                x + size, y - size, z + size,
                x + size, y + size, z + size,
                x - size, y + size, z + size,
                x - size, y - size, z + size,
                0, 0, 5, alpha
        );

        // East
        addQuad(buffer, matrix,
                x + size, y - size, z - size,
                x + size, y + size, z - size,
                x + size, y + size, z + size,
                x + size, y - size, z + size,
                0, 0, 5, alpha
        );

        // West
        addQuad(buffer, matrix,
                x - size, y - size, z + size,
                x - size, y + size, z + size,
                x - size, y + size, z - size,
                x - size, y - size, z - size,
                0, 0, 5, alpha
        );

        BufferUploader.drawWithShader(buffer.buildOrThrow());
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
            double yaw = i * 2.399963D + tickCount * 0.0008D;
            double pitch = -0.85D + Math.floorMod(i * 37, 170) / 100.0D;
            double radius = 22.0D + Math.floorMod(i * 11, 18);

            double flat = Math.cos(pitch);

            Vec3 center = new Vec3(
                    cx + Math.cos(yaw) * flat * radius,
                    cy + Math.sin(pitch) * radius,
                    cz + Math.sin(yaw) * flat * radius
            );

            int pulse = Math.floorMod(tickCount + i * 13, 50);
            float twinkle = pulse < 25
                    ? pulse / 25.0F
                    : (50 - pulse) / 25.0F;

            int alpha = (int) ((60.0F + 120.0F * twinkle) * intensity);
            double size = 0.035D + (i % 5) * 0.012D;

            addLine(
                    buffer,
                    matrix,
                    center.add(-size, 0.0D, 0.0D),
                    center.add(size, 0.0D, 0.0D),
                    145, 165, 255, alpha
            );

            addLine(
                    buffer,
                    matrix,
                    center.add(0.0D, -size, 0.0D),
                    center.add(0.0D, size, 0.0D),
                    145, 165, 255, alpha
            );
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void renderVoidSeams(
            Matrix4f matrix,
            Vec3 cameraPosition,
            int tickCount,
            float intensity
    ) {
        RenderSystem.lineWidth(2.4F);

        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.DEBUG_LINES,
                DefaultVertexFormat.POSITION_COLOR
        );

        double cx = cameraPosition.x;
        double cy = cameraPosition.y;
        double cz = cameraPosition.z;

        for (int i = 0; i < SEAM_COUNT; i++) {
            double yaw = i * TWO_PI / SEAM_COUNT + tickCount * 0.0015D;
            double distance = 7.0D + Math.floorMod(i * 9, 18);
            double height = -4.0D + Math.floorMod(i * 5, 13);

            Vec3 center = new Vec3(
                    cx + Math.cos(yaw) * distance,
                    cy + height,
                    cz + Math.sin(yaw) * distance
            );

            double tiltX = Math.sin((tickCount + i * 31) * 0.018D) * 0.65D;
            double tiltZ = Math.cos((tickCount + i * 17) * 0.015D) * 0.65D;
            double length = 3.5D + Math.floorMod(i * 7, 8);

            Vec3 axis = new Vec3(tiltX, 1.0D, tiltZ).normalize();

            Vec3 start = center.subtract(axis.scale(length * 0.5D));
            Vec3 end = center.add(axis.scale(length * 0.5D));

            int alpha = (int) ((130.0F + Math.floorMod(i * 19, 75)) * intensity);

            addBrokenLine(
                    buffer,
                    matrix,
                    start,
                    end,
                    5 + i % 4,
                    55,
                    64,
                    165,
                    alpha
            );

            if (i % 2 == 0) {
                addBrokenLine(
                        buffer,
                        matrix,
                        start.add(0.045D, 0.0D, 0.045D),
                        end.add(0.045D, 0.0D, 0.045D),
                        5 + i % 4,
                        118,
                        132,
                        245,
                        (int) (alpha * 0.55F)
                );
            }
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.lineWidth(1.0F);
    }

    private static void renderVoidRings(
            Matrix4f matrix,
            Vec3 cameraPosition,
            int tickCount,
            float intensity
    ) {
        RenderSystem.lineWidth(2.0F);

        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.DEBUG_LINES,
                DefaultVertexFormat.POSITION_COLOR
        );

        double cx = cameraPosition.x;
        double cy = cameraPosition.y;
        double cz = cameraPosition.z;

        for (int i = 0; i < RING_COUNT; i++) {
            double yaw = i * TWO_PI / RING_COUNT + tickCount * 0.002D;
            double distance = 5.0D + i * 2.6D;

            Vec3 center = new Vec3(
                    cx + Math.cos(yaw) * distance,
                    cy - 1.1D + Math.sin((tickCount + i * 23) * 0.018D) * 0.4D,
                    cz + Math.sin(yaw) * distance
            );

            double radius = 0.5D + (i % 4) * 0.18D;
            int segments = 24;

            int alpha = (int) ((90.0F + i * 8.0F) * intensity);

            for (int segment = 0; segment < segments; segment++) {
                if ((segment + i) % 4 == 0) continue;

                double a0 = segment * TWO_PI / segments + tickCount * 0.012D;
                double a1 = (segment + 1) * TWO_PI / segments + tickCount * 0.012D;

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
                        64,
                        48,
                        155,
                        alpha
                );
            }
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.lineWidth(1.0F);
    }

    private static void addBrokenLine(
            BufferBuilder buffer,
            Matrix4f matrix,
            Vec3 start,
            Vec3 end,
            int pieces,
            int r,
            int g,
            int b,
            int a
    ) {
        Vec3 diff = end.subtract(start);

        for (int i = 0; i < pieces; i++) {
            if (i % 3 == 1) continue;

            double p0 = i / (double) pieces;
            double p1 = (i + 0.75D) / pieces;

            Vec3 segmentStart = start.add(diff.scale(p0));
            Vec3 segmentEnd = start.add(diff.scale(Math.min(1.0D, p1)));

            addLine(buffer, matrix, segmentStart, segmentEnd, r, g, b, a);
        }
    }

    private static void addQuad(
            BufferBuilder buffer,
            Matrix4f matrix,
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            double x3, double y3, double z3,
            double x4, double y4, double z4,
            int r,
            int g,
            int b,
            int a
    ) {
        buffer.addVertex(matrix, (float) x1, (float) y1, (float) z1).setColor(r, g, b, a);
        buffer.addVertex(matrix, (float) x2, (float) y2, (float) z2).setColor(r, g, b, a);
        buffer.addVertex(matrix, (float) x3, (float) y3, (float) z3).setColor(r, g, b, a);
        buffer.addVertex(matrix, (float) x4, (float) y4, (float) z4).setColor(r, g, b, a);
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
        buffer.addVertex(
                matrix,
                (float) start.x,
                (float) start.y,
                (float) start.z
        ).setColor(r, g, b, a);

        buffer.addVertex(
                matrix,
                (float) end.x,
                (float) end.y,
                (float) end.z
        ).setColor(r, g, b, a);
    }
}