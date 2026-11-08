package com.codechoic3s.blockregexube.client;

import com.codechoic3s.blockregexube.Config;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class Renderer {
    private static final int BATCH_SIZE = 256;

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        if (!Config.ENABLED.get()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        List<BlockPos> blocks = BlockScanner.getMatchedBlocks();
        if (blocks.isEmpty()) return;

        Vec3 cameraPos = event.getCamera().getPosition();
        double camX = cameraPos.x;
        double camY = cameraPos.y;
        double camZ = cameraPos.z;

        float red = Config.RED.get().floatValue();
        float green = Config.GREEN.get().floatValue();
        float blue = Config.BLUE.get().floatValue();
        float alpha = Config.ALPHA.get().floatValue();
        float lineWidth = Config.LINE_WIDTH.get().floatValue();
        boolean throughWalls = Config.RENDER_THROUGH_WALLS.get();

        if (throughWalls) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(lineWidth);
        RenderSystem.disableCull();

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-camX, -camY, -camZ);
        Matrix4f matrix = poseStack.last().pose();

        int total = blocks.size();
        for (int batchStart = 0; batchStart < total; batchStart += BATCH_SIZE) {
            int batchEnd = Math.min(batchStart + BATCH_SIZE, total);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

            for (int i = batchStart; i < batchEnd; i++) {
                BlockPos pos = blocks.get(i);
                AABB box = BlockScanner.getBlockBounds(pos);

                // Frustum culling: не рисуем блоки за пределами видимости камеры
                if (!event.getFrustum().isVisible(box)) continue;

                drawBox(buffer, matrix, box, red, green, blue, alpha);
            }

            tesselator.end();
        }

        poseStack.popPose();

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.lineWidth(1.0f);
    }

    private static void drawBox(BufferBuilder buffer, Matrix4f matrix, AABB box,
                                float r, float g, float b, float a) {
        // Bottom face
        vertex(buffer, matrix, box.minX, box.minY, box.minZ, r, g, b, a);
        vertex(buffer, matrix, box.maxX, box.minY, box.minZ, r, g, b, a);
        vertex(buffer, matrix, box.maxX, box.minY, box.minZ, r, g, b, a);
        vertex(buffer, matrix, box.maxX, box.minY, box.maxZ, r, g, b, a);
        vertex(buffer, matrix, box.maxX, box.minY, box.maxZ, r, g, b, a);
        vertex(buffer, matrix, box.minX, box.minY, box.maxZ, r, g, b, a);
        vertex(buffer, matrix, box.minX, box.minY, box.maxZ, r, g, b, a);
        vertex(buffer, matrix, box.minX, box.minY, box.minZ, r, g, b, a);

        // Top face
        vertex(buffer, matrix, box.minX, box.maxY, box.minZ, r, g, b, a);
        vertex(buffer, matrix, box.maxX, box.maxY, box.minZ, r, g, b, a);
        vertex(buffer, matrix, box.maxX, box.maxY, box.minZ, r, g, b, a);
        vertex(buffer, matrix, box.maxX, box.maxY, box.maxZ, r, g, b, a);
        vertex(buffer, matrix, box.maxX, box.maxY, box.maxZ, r, g, b, a);
        vertex(buffer, matrix, box.minX, box.maxY, box.maxZ, r, g, b, a);
        vertex(buffer, matrix, box.minX, box.maxY, box.maxZ, r, g, b, a);
        vertex(buffer, matrix, box.minX, box.maxY, box.minZ, r, g, b, a);

        // Vertical edges
        vertex(buffer, matrix, box.minX, box.minY, box.minZ, r, g, b, a);
        vertex(buffer, matrix, box.minX, box.maxY, box.minZ, r, g, b, a);
        vertex(buffer, matrix, box.maxX, box.minY, box.minZ, r, g, b, a);
        vertex(buffer, matrix, box.maxX, box.maxY, box.minZ, r, g, b, a);
        vertex(buffer, matrix, box.maxX, box.minY, box.maxZ, r, g, b, a);
        vertex(buffer, matrix, box.maxX, box.maxY, box.maxZ, r, g, b, a);
        vertex(buffer, matrix, box.minX, box.minY, box.maxZ, r, g, b, a);
        vertex(buffer, matrix, box.minX, box.maxY, box.maxZ, r, g, b, a);
    }

    private static void vertex(BufferBuilder buffer, Matrix4f matrix,
                               double x, double y, double z,
                               float r, float g, float b, float a) {
        buffer.vertex(matrix, (float) x, (float) y, (float) z)
                .color(r, g, b, a)
                .endVertex();
    }
}
