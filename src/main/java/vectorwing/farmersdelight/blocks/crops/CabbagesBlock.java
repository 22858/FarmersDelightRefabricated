package vectorwing.farmersdelight.blocks.crops;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import vectorwing.farmersdelight.registry.ModBlocks;
import vectorwing.farmersdelight.registry.ModItems;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class CabbagesBlock extends CropBlock
{
	private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D)
	};

	public CabbagesBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockState getPlant(BlockGetter world, BlockPos pos) {
		return ModBlocks.CABBAGE_CROP.get().defaultBlockState();
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return ModItems.CABBAGE_SEEDS.get();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_AGE[state.getValue(this.getAgeProperty())];
	}
}
