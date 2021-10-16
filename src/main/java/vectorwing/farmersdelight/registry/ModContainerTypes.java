package vectorwing.farmersdelight.registry;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.tile.container.CookingPotContainer;

public class ModContainerTypes
{
	public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, FarmersDelight.MODID);

	public static final RegistryObject<MenuType<CookingPotContainer>> COOKING_POT = CONTAINER_TYPES
			.register("cooking_pot", () -> IForgeContainerType.create(CookingPotContainer::new));
}
