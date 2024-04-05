package vectorwing.farmersdelight.common.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;

;

public class MushroomColonyItem extends BlockItem
{
	public MushroomColonyItem(Block blockIn, Properties properties) {
		super(blockIn, properties);
	}

	@Override
	@Nullable
	protected BlockState getPlacementState(BlockPlaceContext context) {
		BlockState originalState = this.getBlock().getStateForPlacement(context);
		if (originalState != null) {
			BlockState matureState = originalState.setValue(MushroomColonyBlock.COLONY_AGE, 3);
			return this.canPlace(context, matureState) ? matureState : null;
		}
		return null;
	}
}

