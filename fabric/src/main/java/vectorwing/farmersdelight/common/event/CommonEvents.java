package vectorwing.farmersdelight.common.event;

import com.mojang.datafixers.util.Pair;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityUseItemEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.FoodValues;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.ModTags;

public class CommonEvents {

    public static void init(){
        LivingEntityUseItemEvents.LIVING_USE_ITEM_FINISH.register(CommonEvents::handleVanillaSoupEffects);
        EntityEvents.ON_JOIN_WORLD.register(CommonEvents::onAnimalsJoinWorld);
    }



    private static ItemStack handleVanillaSoupEffects(LivingEntity entity, ItemStack itemStack, int i, ItemStack result) {
        Item food = itemStack.getItem();

        if (Configuration.RABBIT_STEW_JUMP_BOOST.get() && food.equals(Items.RABBIT_STEW)) {
            entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 200, 1));
        }

        if (Configuration.VANILLA_SOUP_EXTRA_EFFECTS.get()) {
            FoodProperties soupEffects = FoodValues.VANILLA_SOUP_EFFECTS.get(food);

            if (soupEffects != null) {
                for (Pair<MobEffectInstance, Float> pair : soupEffects.getEffects()) {
                    entity.addEffect(pair.getFirst());
                }
            }
        }
        return result;
    }

    private static boolean onAnimalsJoinWorld(Entity entity, Level level, boolean b) {
        if (entity instanceof PathfinderMob mob) {
            if (mob.getType().is(ModTags.HORSE_FEED_TEMPTED)) {
                int priority = getTemptGoalPriority(mob);
                if (priority >= 0)
                    mob.goalSelector.addGoal(priority, new TemptGoal(mob, 1.25D, Ingredient.of(ModItems.HORSE_FEED.get()), false));
            }
            if (mob instanceof Rabbit rabbit) {
                int priority = getTemptGoalPriority(rabbit);
                if (priority >= 0)
                    rabbit.goalSelector.addGoal(priority, new TemptGoal(rabbit, 1.0D, Ingredient.of(ModItems.CABBAGE.get(), ModItems.CABBAGE_LEAF.get()), false));
            }
        }
    }

    public static int getTemptGoalPriority(Mob mob) {
        return mob.goalSelector.getAvailableGoals().stream()
                .filter(goal -> goal.getGoal() instanceof TemptGoal)
                .findFirst()
                .map(WrappedGoal::getPriority)
                .orElse(-1);
    }


    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void onSneakPlaceTool(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        ItemStack heldStack = player.getMainHandItem();
        BlockEntity tileEntity = level.getBlockEntity(event.getPos());

        if (player.isSecondaryUseActive() && !heldStack.isEmpty() && tileEntity instanceof CuttingBoardBlockEntity) {
            if (heldStack.getItem() instanceof TieredItem ||
                    heldStack.getItem() instanceof TridentItem ||
                    heldStack.getItem() instanceof ShearsItem) {
                boolean success = ((CuttingBoardBlockEntity) tileEntity).carveToolOnBoard(player.getAbilities().instabuild ? heldStack.copy() : heldStack);
                if (success) {
                    level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
            }
        }
    }

}
