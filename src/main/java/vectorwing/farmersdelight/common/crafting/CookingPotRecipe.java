package vectorwing.farmersdelight.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.google.gson.JsonParseException;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import io.github.fabricators_of_create.porting_lib.util.CraftingHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.client.recipebook.CookingPotRecipeBookTab;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModRecipeSerializers;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.EnumSet;

;

public class CookingPotRecipe implements Recipe<RecipeWrapper>
{
	public static final int INPUT_SLOTS = 6;

	private final ResourceLocation id;
	private final String group;
	private final CookingPotRecipeBookTab tab;
	private final NonNullList<Ingredient> inputItems;
	private final ItemStack output;
	private final ItemStack container;
	private final ItemStack containerOverride;
	private final float experience;
	private final int cookTime;

	public CookingPotRecipe(ResourceLocation id, String group, @Nullable CookingPotRecipeBookTab tab, NonNullList<Ingredient> inputItems, ItemStack output, ItemStack container, float experience, int cookTime) {
		this.id = id;
		this.group = group;
		this.tab = tab;
		this.inputItems = inputItems;
		this.output = output;

		if (!container.isEmpty()) {
			this.container = container;
		} else if (!output.getRecipeRemainder().isEmpty()) {
			this.container = output.getRecipeRemainder();
		} else {
			this.container = ItemStack.EMPTY;
		}

		this.containerOverride = container;
		this.experience = experience;
		this.cookTime = cookTime;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Nullable
	public CookingPotRecipeBookTab getRecipeBookTab() {
		return this.tab;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return this.inputItems;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider provider) {
		return this.output;
	}

	public ItemStack getOutputContainer() {
		return this.container;
	}

	public ItemStack getContainerOverride() {
		return this.containerOverride;
	}

	@Override
	public ItemStack assemble(RecipeWrapper inv, HolderLookup.Provider provider) {
		return this.output.copy();
	}

	public float getExperience() {
		return this.experience;
	}

	public int getCookTime() {
		return this.cookTime;
	}

	@Override
	public boolean matches(RecipeWrapper inv, Level level) {
		StackedContents stackedContents = new StackedContents();
		int i = 0;

		for (int j = 0; j < INPUT_SLOTS; ++j) {
			ItemStack itemstack = inv.getItem(j);
			if (!itemstack.isEmpty()) {
				++i;
				stackedContents.accountStack(itemstack, 1);
			}
		}
		return i == this.inputItems.size() && stackedContents.canCraft(this, null);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= this.inputItems.size();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipeSerializers.COOKING.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipeTypes.COOKING.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModItems.COOKING_POT.get());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CookingPotRecipe that = (CookingPotRecipe) o;

		if (Float.compare(that.getExperience(), getExperience()) != 0) return false;
		if (getCookTime() != that.getCookTime()) return false;
		if (!getId().equals(that.getId())) return false;
		if (!getGroup().equals(that.getGroup())) return false;
		if (tab != that.tab) return false;
		if (!inputItems.equals(that.inputItems)) return false;
		if (!output.equals(that.output)) return false;
		return container.equals(that.container);
	}

	@Override
	public int hashCode() {
		int result = getId().hashCode();
		result = 31 * result + getGroup().hashCode();
		result = 31 * result + (getRecipeBookTab() != null ? getRecipeBookTab().hashCode() : 0);
		result = 31 * result + inputItems.hashCode();
		result = 31 * result + output.hashCode();
		result = 31 * result + container.hashCode();
		result = 31 * result + (getExperience() != 0.0f ? Float.floatToIntBits(getExperience()) : 0);
		result = 31 * result + getCookTime();
		return result;
	}

	public static class Serializer implements RecipeSerializer<CookingPotRecipe>
	{
		private static final MapCodec<CookingPotRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(CookingPotRecipe::getGroup),
				CookingPotRecipeBookTab.CODEC.optionalFieldOf("recipe_book_tab").xmap(optional -> optional.orElse(null), Optional::of).forGetter(CookingPotRecipe::getRecipeBookTab),
				Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").xmap(ingredients -> {
					NonNullList<Ingredient> nonNullList = NonNullList.create();
					nonNullList.addAll(ingredients);
					return nonNullList;
				}, ingredients -> ingredients).forGetter(CookingPotRecipe::getIngredients),
				ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.output),
				ItemStack.STRICT_CODEC.optionalFieldOf("container", ItemStack.EMPTY).forGetter(CookingPotRecipe::getContainerOverride),
				Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(CookingPotRecipe::getExperience),
				Codec.INT.optionalFieldOf("cookingtime", 200).forGetter(CookingPotRecipe::getCookTime)
		).apply(inst, CookingPotRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> STREAM_CODEC = StreamCodec.of(CookingPotRecipe.Serializer::toNetwork, CookingPotRecipe.Serializer::fromNetwork);

		public Serializer() {
		}

		@Override
		public MapCodec<CookingPotRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		private static CookingPotRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
			String groupIn = buffer.readUtf();
			CookingPotRecipeBookTab tabIn = CookingPotRecipeBookTab.findByName(buffer.readUtf());
			int i = buffer.readVarInt();
			NonNullList<Ingredient> inputItemsIn = NonNullList.withSize(i, Ingredient.EMPTY);

			inputItemsIn.replaceAll(ignored -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));

			ItemStack outputIn = ItemStack.STREAM_CODEC.decode(buffer);
			ItemStack container = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
			float experienceIn = buffer.readFloat();
			int cookTimeIn = buffer.readVarInt();
			return new CookingPotRecipe(recipeId, groupIn, tabIn, inputItemsIn, outputIn, container, experienceIn, cookTimeIn);
		}

		private static void toNetwork(RegistryFriendlyByteBuf buffer, CookingPotRecipe recipe) {
			buffer.writeUtf(recipe.group);
			buffer.writeUtf(recipe.tab != null ? recipe.tab.toString() : "");
			buffer.writeVarInt(recipe.inputItems.size());

			for (Ingredient ingredient : recipe.inputItems) {
				Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
			}

			ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
			ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.container);
			buffer.writeFloat(recipe.experience);
			buffer.writeVarInt(recipe.cookTime);
		}
	}
}
