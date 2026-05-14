package net.thejadeproject.ascension.refactor_packages.skills.vfx.weaponvfx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.AscensionCraft;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WeaponSwingVfxRenderer extends EntityRenderer<WeaponSwingVfxEntity> {

    private static final int FRAME_COUNT = 7;
    private static final int FRAME_TICKS = 2;

    private static final Map<String, ResourceLocation[]> TEXTURE_CACHE = new ConcurrentHashMap<>();

    public WeaponSwingVfxRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.0f;
    }

    // ── Main render entry ────────────────────────────────────────────────────

    @Override
    public void render(@NotNull WeaponSwingVfxEntity entity,
                       float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource,
                       int packedLight) {

        poseStack.pushPose();

        String texPath = entity.getTexPath();
        Vector3f scale = entity.getRadius();
        float rotZ = entity.getRotationZ();
        float xRot = entity.getXRot();
        float yRot = entity.getYRot();
        int frame = (entity.tickCount / FRAME_TICKS) % FRAME_COUNT;

        ResourceLocation texture = getTextures(texPath)[frame];

        applyRotation(poseStack, xRot, yRot, rotZ);
        poseStack.scale(scale.x(), scale.y(), scale.z());

        VertexConsumer consumer = bufferSource.getBuffer(
                RenderType.entityTranslucentEmissive(texture));

        drawQuad(poseStack, consumer, packedLight, rotZ);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, 15728880);
    }

    // ── Quad ─────────────────────────────────────────────────────────────────

    private static void drawQuad(PoseStack poseStack, VertexConsumer consumer,
                                 int light, float rotZ) {
        PoseStack.Pose pose = poseStack.last();
        Vector3f n = normalDirection(rotZ);

        float[][] corners = {
                { -0.5f, 0f, -0.5f, 0f, 1f },
                {  0.5f, 0f, -0.5f, 1f, 1f },
                {  0.5f, 0f, 0.5f, 1f, 0f },
                { -0.5f, 0f, 0.5f, 0f, 0f },
        };

        for (float[] v : corners) {
            consumer.addVertex(pose, v[0], v[1], v[2])
                    .setColor(1f, 1f, 1f, 1f)
                    .setUv(v[3], v[4])
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal(pose, n.x(), n.y(), n.z());
        }
    }

    // ── Rotation ─────────────────────────────────────────────────────────────

    private static void applyRotation(PoseStack poseStack, float xRot, float yRot, float zRot) {
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        Quaternionf q = new Quaternionf()
                .rotationY((float) Math.toRadians(-yRot))
                .mul(new Quaternionf().rotationX((float) Math.toRadians(xRot)))
                .mul(new Quaternionf().rotationZ((float) Math.toRadians(zRot)));
        matrix.rotate(q);
        normal.rotate(q);
    }

    private static Vector3f normalDirection(float rotZ) {
        if (rotZ > -80 && rotZ < 80) return new Vector3f( 0,  1,  0);
        if (rotZ > 70 && rotZ < 110) return new Vector3f( 1,  0,  0);
        if (rotZ > -110 && rotZ < -70) return new Vector3f(-1,  0,  0);
        return new Vector3f( 0, -1,  0);
    }

    // ── Texture cache ─────────────────────────────────────────────────────────

    /**
     * Builds and caches an array of 7 ResourceLocations for the given texture path.
     * @param texPath e.g. "entity/vfx/sword_swing/red"
     */
    private static ResourceLocation[] getTextures(String texPath) {
        return TEXTURE_CACHE.computeIfAbsent(texPath, key -> {
            ResourceLocation[] arr = new ResourceLocation[FRAME_COUNT];
            for (int i = 0; i < FRAME_COUNT; i++) {
                arr[i] = ResourceLocation.fromNamespaceAndPath(
                        AscensionCraft.MOD_ID,
                        "textures/" + key + "/" + i + ".png");
            }
            return arr;
        });
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull WeaponSwingVfxEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
                AscensionCraft.MOD_ID,
                "textures/" + entity.getTexPath() + "/0.png");
    }
}
