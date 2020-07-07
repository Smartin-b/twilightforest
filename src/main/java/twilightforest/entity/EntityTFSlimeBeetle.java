package twilightforest.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import twilightforest.entity.projectile.EntityTFSlimeProjectile;

public class EntityTFSlimeBeetle extends MonsterEntity implements IRangedAttackMob {

	public EntityTFSlimeBeetle(EntityType<? extends EntityTFSlimeBeetle> type, World world) {
		super(type, world);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, PlayerEntity.class, 3.0F, 1.25F, 2.0F));
		this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1, 30, 10));
		this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
	}

	protected static AttributeModifierMap.MutableAttribute registerAttributes() {
		return MonsterEntity.func_234295_eP_()
				.func_233815_a_(Attributes.field_233818_a_, 25.0D)
				.func_233815_a_(Attributes.field_233821_d_, 0.23D)
				.func_233815_a_(Attributes.field_233823_f_, 4);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SPIDER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SPIDER_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState block) {
		playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
	}

	@Override
	public float getEyeHeight(Pose pose) {
		return 0.25F;
	}

	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.ARTHROPOD;
	}

	@Override
	public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
		ThrowableEntity projectile = new EntityTFSlimeProjectile(TFEntities.slime_blob, this.world, this);
		playSound(SoundEvents.ENTITY_SLIME_SQUISH_SMALL, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
		double tx = target.getPosX() - this.getPosX();
		double ty = target.getPosY() + target.getEyeHeight() - 1.100000023841858D - projectile.getPosY();
		double tz = target.getPosZ() - this.getPosZ();
		float heightOffset = MathHelper.sqrt(tx * tx + tz * tz) * 0.2F;
		projectile.shoot(tx, ty + heightOffset, tz, 0.6F, 6.0F);
		this.world.addEntity(projectile);
	}
}
