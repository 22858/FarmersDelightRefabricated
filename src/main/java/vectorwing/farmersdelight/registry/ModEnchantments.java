package vectorwing.farmersdelight.registry;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.enchantments.BackstabbingEnchantment;
import vectorwing.farmersdelight.items.KnifeItem;

public class ModEnchantments
{
	public static final EnchantmentCategory KNIFE = EnchantmentCategory.create("knife", item -> item instanceof KnifeItem);

	public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, FarmersDelight.MODID);

	public static final RegistryObject<Enchantment> BACKSTABBING = ENCHANTMENTS.register("backstabbing",
			() -> new BackstabbingEnchantment(Enchantment.Rarity.UNCOMMON, KNIFE, EquipmentSlot.MAINHAND));
}
