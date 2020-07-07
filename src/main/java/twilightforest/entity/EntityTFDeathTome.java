package twilightforest.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import twilightforest.TFSounds;
import twilightforest.TwilightForestMod;
import twilightforest.entity.projectile.EntityTFTomeBolt;

import javax.annotation.Nullable;

public class EntityTFDeathTome extends MonsterEntity implements IRangedAttackMob {

	public static final ResourceLocation HURT_LOOT_TABLE = TwilightForestMod.prefix("entities/death_tome_hurt");

	public EntityTFDeathTome(EntityType<? extends EntityTFDeathTome> type, World world) {
		super(type, world);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(4, new RangedAttackGoal(this, 1, 60, 10));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
	}

	protected static AttributeModifierMap.MutableAttribute registerAttributes() {
		return MonsterEntity.func_234295_eP_()
				.func_233815_a_(Attributes.field_233818_a_, 30.0D)
				.func_233815_a_(Attributes.field_233821_d_, 0.25D)
				.func_233815_a_(Attributes.field_233823_f_, 4);
	}

	@Override
	public void livingTick() {
		super.livingTick();

		for (int i = 0; i < 1; ++i) {
			this.world.addParticle(ParticleTypes.ENCHANT, this.getPosX() + (this.rand.nextDouble() - 0.5D) * this.getWidth(), this.getPosY() + this.rand.nextDouble() * (this.getHeight() - 0.75D) + 0.5D, this.getPosZ() + (this.rand.nextDouble() - 0.5D) * this.getWidth(),
					0, 0.5, 0);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource src, float damage) {
		if (src.isFireDamage()) {
			damage *= 2;
		}

		if (super.attackEntityFrom(src, damage)) {
			if (!world.isRemote) {
				LootContext ctx = getLootContextBuilder(true, src).build(LootParameterSets.ENTITY);

				world.getServer().getLootTableManager().getLootTableFromLocation(HURT_LOOT_TABLE).generate(ctx, s -> entityDropItem(s, 1.0F));
			}
			return true;
		} else {
			return false;
		}
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return TFSounds.TOME_IDLE;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return TFSounds.TOME_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return TFSounds.TOME_DEATH;
	}

	@Override
	public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
		ThrowableEntity projectile = new EntityTFTomeBolt(TFEntities.tome_bolt, this.world, this);
		double tx = target.getPosX() - this.getPosX();
		double ty = target.getPosY() + target.getEyeHeight() - 1.100000023841858D - projectile.getPosY();
		double tz = target.getPosZ() - this.getPosZ();
		float heightOffset = MathHelper.sqrt(tx * tx + tz * tz) * 0.2F;
		projectile.shoot(tx, ty + heightOffset, tz, 0.6F, 6.0F);
		this.world.addEntity(projectile);
	}
}
