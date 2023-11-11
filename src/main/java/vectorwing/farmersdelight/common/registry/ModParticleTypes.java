package vectorwing.farmersdelight.common.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;
import vectorwing.farmersdelight.FarmersDelight;

public class ModParticleTypes
{
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, FarmersDelight.MODID);

	public static final RegistryObject<SimpleParticleType> STAR = PARTICLE_TYPES.register("star",
			() -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> STEAM = PARTICLE_TYPES.register("steam",
			() -> new SimpleParticleType(true));
}
