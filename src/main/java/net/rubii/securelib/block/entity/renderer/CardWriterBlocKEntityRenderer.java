package net.rubii.securelib.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.rubii.securelib.block.entity.CardWriterBlockEntity;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class CardWriterBlocKEntityRenderer implements BlockEntityRenderer<CardWriterBlockEntity> {
    public CardWriterBlocKEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(CardWriterBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packetLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = blockEntity.inventory.getStackInSlot(blockEntity.INPUT_SLOT);

        poseStack.pushPose();
        poseStack.scale(0.35F, 0.35F, 0.35F);

        switch (blockEntity.getBlockState().getValue(FACING)) {
            case NORTH: {
                //poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                //poseStack.mulPose(Axis.YP.rotationDegrees(17.0F));
                poseStack.translate(0F, 0.16F, 0F);
            } case SOUTH: {
                //poseStack.mulPose(Axis.YP.rotationDegrees(17.0F));
                poseStack.translate(0F, 0.25F, 0F);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            } case WEST: {
                //poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                //poseStack.mulPose(Axis.YP.rotationDegrees(17.0F));
                poseStack.translate(0F, 0.16F, 0F);
            } case EAST: {
                //poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                //poseStack.mulPose(Axis.YP.rotationDegrees(17.0F));
                poseStack.translate(0F, 0.16F, 0F);
            }
        }


        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos()),
                OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 1);

        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos blockPos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, blockPos);
        int skyLight = level.getBrightness(LightLayer.SKY, blockPos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
