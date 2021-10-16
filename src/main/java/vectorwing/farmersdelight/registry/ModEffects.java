package vectorwing.farmersdelight.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.effects.ComfortEffect;
import vectorwing.farmersdelight.effects.NourishmentEffect;

public class ModEffects
{
	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, FarmersDelight.MODID);

	public static final RegistryObject<MobEffect> NOURISHMENT = EFFECTS.register("nourishment", NourishmentEffect::new);
	public static final RegistryObject<MobEffect> COMFORT = EFFECTS.register("comfort", ComfortEffect::new);
}
