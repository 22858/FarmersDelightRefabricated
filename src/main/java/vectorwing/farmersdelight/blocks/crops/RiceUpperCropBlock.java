package vectorwing.farmersdelight.blocks.crops;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import vectorwing.farmersdelight.registry.ModBlocks;
import vectorwing.farmersdelight.registry.ModItems;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class RiceUpperCropBlock extends CropBlock
{
	public static final IntegerProperty RICE_AGE = BlockStateProperties.AGE_3;
	private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
			Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D),
			Block.box(3.0D, 0.0D, 3.0D, 13.0D, 10.0D, 13.0D),
			Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D),
			Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D)
	};

	public RiceUpperCropBlock(Properties properties) {
		super(properties);
	}

	@Override
	public IntegerProperty getAgeProperty() {
		return RICE_AGE;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_AGE[state.getValue(this.getAgeProperty())];
	}

	@Override
	public int getMaxAge() {
		return 3;
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return ModItems.RICE.get();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(RICE_AGE);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return state.getBlock() == ModBlocks.RICE_CROP.get();
	}

	@Override
	protected int getBonemealAgeIncrease(Level worldIn) {
		return super.getBonemealAgeIncrease(worldIn) / 3;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		return (worldIn.getRawBrightness(pos, 0) >= 8 || worldIn.canSeeSky(pos)) && worldIn.getBlockState(pos.below()).getBlock() == ModBlocks.RICE_CROP.get();
	}
}
