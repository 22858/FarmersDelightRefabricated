// TODO: As usual, overlay stuff is broken. Fix it later!

package vectorwing.farmersdelight.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.registry.ModEffects;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ComfortHealthOverlay
{
	protected static int healthIconsOffset;
	private static final ResourceLocation MOD_ICONS_TEXTURE = new ResourceLocation(FarmersDelight.MODID, "textures/gui/fd_icons.png");

	private static final Random HUD_RANDOM = new Random();

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new ComfortHealthOverlay());
	}

	@SubscribeEvent
	public void onRenderGuiOverlayPost(RenderGameOverlayEvent.PostLayer event) {
		if (event.getOverlay() == ForgeIngameGui.PLAYER_HEALTH_ELEMENT) {
			Minecraft mc = Minecraft.getInstance();
			ForgeIngameGui gui = (ForgeIngameGui) mc.gui;
			if (!mc.options.hideGui && gui.shouldDrawSurvivalElements()) {
				renderComfortOverlay(gui, event.getMatrixStack());
			}
		}
	}

	public static void renderComfortOverlay(ForgeIngameGui gui, PoseStack poseStack) {
		if (!Configuration.COMFORT_HEALTH_OVERLAY.get()) {
			return;
		}

		healthIconsOffset = gui.left_height;
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;

		if (player == null) {
			return;
		}

		FoodData stats = player.getFoodData();
		int top = minecraft.getWindow().getGuiScaledHeight() - healthIconsOffset + 10;
		int left = minecraft.getWindow().getGuiScaledWidth() / 2 - 91;

		boolean isPlayerEligibleForComfort = stats.getSaturationLevel() == 0.0F
				&& player.isHurt()
				&& !player.hasEffect(MobEffects.REGENERATION);

		if (player.getEffect(ModEffects.COMFORT.get()) != null && isPlayerEligibleForComfort) {
			drawComfortOverlay(player, minecraft, poseStack, left, top);
		}
	}

	public static void drawComfortOverlay(Player player, Minecraft minecraft, PoseStack matrixStack, int left, int top) {
		int ticks = minecraft.gui.getGuiTicks();
		Random rand = new Random();
		rand.setSeed(ticks * 312871L);

		int health = Mth.ceil(player.getHealth());
		float absorb = Mth.ceil(player.getAbsorptionAmount());
		AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
		float healthMax = (float) attrMaxHealth.getValue();

		int regen = -1;
		if (player.hasEffect(MobEffects.REGENERATION)) regen = ticks % 25;

		int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
		int rowHeight = Math.max(10 - (healthRows - 2), 3);

		int comfortSheen = ticks % 50;
		int comfortHeartFrame = comfortSheen % 2;
		int[] textureWidth = {5, 9};

		RenderSystem.setShaderTexture(0, MOD_ICONS_TEXTURE);
		RenderSystem.enableBlend();

		int healthMaxSingleRow = Mth.ceil(Math.min(healthMax, 20) / 2.0F);
		int leftHeightOffset = ((healthRows - 1) * rowHeight); // This keeps the overlay on the bottommost row of hearts

		for (int i = 0; i < healthMaxSingleRow; ++i) {
			int column = i % 10;
			int x = left + column * 8;
			int y = top + leftHeightOffset;

			if (health <= 4) y += rand.nextInt(2);
			if (i == regen) y -= 2;

			if (column == comfortSheen / 2) {
				minecraft.gui.blit(matrixStack, x, y, 0, 9, textureWidth[comfortHeartFrame], 9);
			}
			if (column == (comfortSheen / 2) - 1 && comfortHeartFrame == 0) {
				minecraft.gui.blit(matrixStack, x + 5, y, 5, 9, 4, 9);
			}
		}

		RenderSystem.disableBlend();
		RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
	}

//	private static void generateHealthBarOffsets(int top, int left, int right, int ticks, Player player) {
//		HUD_RANDOM.setSeed((long) (ticks * 312871L));
//
//		final int preferHealthBars = 10;
//		final float maxHealth = player.getMaxHealth();
//		final float absorptionHealth = (float) Math.ceil(player.getAbsorptionAmount());
//
//		if (!Float.isFinite(maxHealth + absorptionHealth)) {
//			healthBarOffsets.setSize(0);
//			return;
//		}
//
//		int healthBars = (int) Math.ceil((maxHealth + absorptionHealth) / 2.0F);
//		int healthRows = (int) Math.ceil((float) healthBars / 10.0F);
//
//		int healthRowHeight = Math.max(10 - (healthRows - 2), 3);
//
//		boolean shouldAnimatedHealth = false;
//
//		if (Configuration.COMFORT_HEALTH_OVERLAY.get()) {
//			shouldAnimatedHealth = Math.ceil(player.getHealth()) <= 4;
//		}
//
//		if (healthBarOffsets.size() != healthBars)
//			healthBarOffsets.setSize(healthBars);
//
//		for (int i = healthBars - 1; i >= 0; --i) {
//			int row = (int) Math.ceil((float) (i + 1) / (float) preferHealthBars) - 1;
//			int x = left + i % preferHealthBars * 8;
//			int y = top - row * healthRowHeight;
//			// apply the animated offset
//			if (shouldAnimatedHealth)
//				y += HUD_RANDOM.nextInt(2);
//
//			// reuse the point object to reduce memory usage
//			IntPoint point = healthBarOffsets.get(i);
//			if (point == null) {
//				point = new IntPoint();
//				healthBarOffsets.set(i, point);
//			}
//
//			point.x = x - left;
//			point.y = y - top;
//		}
//	}
}
