package vectorwing.farmersdelight.client.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import vectorwing.farmersdelight.blocks.StoveBlock;
import vectorwing.farmersdelight.tile.SkilletTileEntity;

import java.util.Random;

public class SkilletRenderer implements BlockEntityRenderer<SkilletTileEntity>
{
	private final Random random = new Random();

	public SkilletRenderer(BlockEntityRendererProvider.Context pContext) {
	}

	@Override
	public void render(SkilletTileEntity skilletEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		Direction direction = skilletEntity.getBlockState().getValue(StoveBlock.FACING);
		IItemHandler inventory = skilletEntity.getInventory();
		int posLong = (int) skilletEntity.getBlockPos().asLong();

		ItemStack stack = inventory.getStackInSlot(0);
		int seed = stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
		this.random.setSeed(seed);

		if (!stack.isEmpty()) {
			int itemRenderCount = this.getModelCount(stack);
			for (int i = 0; i < itemRenderCount; i++) {
				poseStack.pushPose();

				// Stack up items in the skillet, with a slight offset per item
				float xOffset = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
				float zOffset = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
				poseStack.translate(0.5D + xOffset, 0.1D + 0.03 * (i + 1), 0.5D + zOffset);

				// Rotate item to face the skillet's front side
				float degrees = -direction.toYRot();
				poseStack.mulPose(Vector3f.YP.rotationDegrees(degrees));

				// Rotate item flat on the skillet. Use X and Y from now on
				poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));

				// Resize the items
				poseStack.scale(0.5F, 0.5F, 0.5F);

				if (skilletEntity.getLevel() != null)
					Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, posLong);
				poseStack.popPose();
			}
		}
	}

	protected int getModelCount(ItemStack stack) {
		if (stack.getCount() > 48) {
			return 5;
		} else if (stack.getCount() > 32) {
			return 4;
		} else if (stack.getCount() > 16) {
			return 3;
		} else if (stack.getCount() > 1) {
			return 2;
		}
		return 1;
	}
}
