package vectorwing.farmersdelight.client.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StarParticle extends TextureSheetParticle
{
	protected StarParticle(ClientLevel world, double posX, double posY, double posZ) {
		super(world, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
		this.xd *= (double) 0.01F;
		this.yd *= (double) 0.01F;
		this.zd *= (double) 0.01F;
		this.yd += 0.1D;
		this.quadSize *= 1.5F;
		this.lifetime = 16;
		this.hasPhysics = false;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public float getQuadSize(float scaleFactor) {
		return this.quadSize * Mth.clamp(((float) this.age + scaleFactor) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime) {
			this.remove();
		} else {
			this.move(this.xd, this.yd, this.zd);
			if (this.y == this.yo) {
				this.xd *= 1.1D;
				this.zd *= 1.1D;
			}

			this.xd *= (double) 0.86F;
			this.yd *= (double) 0.86F;
			this.zd *= (double) 0.86F;
			if (this.onGround) {
				this.xd *= (double) 0.7F;
				this.zd *= (double) 0.7F;
			}

		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements ParticleProvider<SimpleParticleType>
	{
		private final SpriteSet spriteSet;

		public Factory(SpriteSet sprite) {
			this.spriteSet = sprite;
		}

		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			StarParticle particle = new StarParticle(worldIn, x, y + 0.3D, z);
			particle.pickSprite(this.spriteSet);
			particle.setColor(1.0F, 1.0F, 1.0F);
			return particle;
		}
	}
}
