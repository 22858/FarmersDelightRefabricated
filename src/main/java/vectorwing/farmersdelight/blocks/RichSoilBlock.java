package vectorwing.farmersdelight.blocks;

import net.minecraft.block.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolType;
import vectorwing.farmersdelight.registry.ModBlocks;
import vectorwing.farmersdelight.setup.Configuration;
import vectorwing.farmersdelight.utils.MathUtils;
import vectorwing.farmersdelight.utils.tags.ModTags;

import javax.annotation.Nullable;
import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("deprecation")
public class RichSoilBlock extends Block
{
	public static final int COLONY_FORMING_LIGHT_LEVEL = 12;

	public RichSoilBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
		if (!worldIn.isClientSide) {
			BlockPos abovePos = pos.above();
			BlockState aboveState = worldIn.getBlockState(abovePos);
			Block aboveBlock = aboveState.getBlock();

			// Do nothing if the plant is unaffected by rich soil
			if (ModTags.UNAFFECTED_BY_RICH_SOIL.contains(aboveBlock) || aboveBlock instanceof TallFlowerBlock) {
				return;
			}

			// Convert mushrooms to colonies if it's dark enough
			if (aboveBlock == Blocks.BROWN_MUSHROOM) {
				if (worldIn.getRawBrightness(pos.above(), 0) <= COLONY_FORMING_LIGHT_LEVEL) {
					worldIn.setBlockAndUpdate(pos.above(), ModBlocks.BROWN_MUSHROOM_COLONY.get().defaultBlockState());
				}
				return;
			}
			if (aboveBlock == Blocks.RED_MUSHROOM) {
				if (worldIn.getRawBrightness(pos.above(), 0) <= COLONY_FORMING_LIGHT_LEVEL) {
					worldIn.setBlockAndUpdate(pos.above(), ModBlocks.RED_MUSHROOM_COLONY.get().defaultBlockState());
				}
				return;
			}

			if (Configuration.RICH_SOIL_BOOST_CHANCE.get() == 0.0) {
				return;
			}

			// If all else fails, and it's a plant, give it a growth boost now and then!
			if (aboveBlock instanceof BonemealableBlock && MathUtils.RAND.nextFloat() <= Configuration.RICH_SOIL_BOOST_CHANCE.get()) {
				BonemealableBlock growable = (BonemealableBlock) aboveBlock;
				if (growable.isValidBonemealTarget(worldIn, pos.above(), aboveState, false) && ForgeHooks.onCropsGrowPre(worldIn, pos.above(), aboveState, true)) {
					growable.performBonemeal(worldIn, worldIn.random, pos.above(), aboveState);
					worldIn.levelEvent(2005, pos.above(), 0);
					ForgeHooks.onCropsGrowPost(worldIn, pos.above(), aboveState);
				}
			}
		}
	}

	@Override
	@Nullable
	public BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolType toolType) {
		return toolType == ToolType.HOE ? ModBlocks.RICH_SOIL_FARMLAND.get().defaultBlockState() : null;
	}


	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
		net.minecraftforge.common.PlantType plantType = plantable.getPlantType(world, pos.relative(facing));
		return plantType != PlantType.CROP && plantType != PlantType.NETHER;
	}
}
