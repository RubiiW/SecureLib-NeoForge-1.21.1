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
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.rubii.securelib.block.entity.CardWriterBlockEntity;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class CardWriterBlocKEntityRenderer implements BlockEntityRenderer<CardWriterBlockEntity> {
    public CardWriterBlocKEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(CardWriterBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packetLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = blockEntity.inventory.getStackInSlot(blockEntity.INPUT_SLOT);

        float x = switch (blockEntity.getBlockState().getValue(FACING)){
            case EAST -> 0.25f;
            case WEST -> 0.775f;
            case SOUTH -> 0.2f;
            default -> 0.8125f;
        };

        float z = switch (blockEntity.getBlockState().getValue(FACING)){
            case EAST -> 0.8125f;
            case WEST -> 0.2f;
            case SOUTH -> 0.25f;
            default -> 0.775f;
        };

        poseStack.pushPose();

        poseStack.translate(x, 0.04, z);

        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

        if (blockEntity.getBlockState().getValue(FACING) == Direction.NORTH || blockEntity.getBlockState().getValue(FACING) == Direction.SOUTH){
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }

        poseStack.scale(0.35F, 0.35F, 0.35F);

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
