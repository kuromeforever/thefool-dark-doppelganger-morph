package io.github.kuromeforever.thefooldarkdoppelgangermorph.runtime;

import net.bandit.darkdoppelganger.entity.DarkDoppelgangerMinionEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class DarkDoppelgangerCombat {
    private DarkDoppelgangerCombat() {
    }

    public static LivingEntity findNearestAhead(Player player, double reach, double radius) {
        Vec3 look = player.getLookAngle().normalize();
        AABB hitBox = player.getBoundingBox().expandTowards(look.scale(reach)).inflate(radius);
        return player.level().getEntitiesOfClass(
                        LivingEntity.class,
                        hitBox,
                        candidate -> isValidTarget(player, candidate)
                                && player.hasLineOfSight(candidate)
                                && isAhead(player, candidate, look, radius)
                ).stream()
                .min(Comparator.<LivingEntity>comparingDouble(player::distanceToSqr)
                        .thenComparing(candidate -> candidate.getUUID().toString()))
                .orElse(null);
    }

    public static boolean hurtFrozenTarget(
            ServerPlayer player,
            LivingEntity target,
            double reach,
            double radius,
            float damage,
            double knockback
    ) {
        if (!isValidTarget(player, target)
                || player.distanceToSqr(target) > (reach + radius) * (reach + radius)
                || !player.hasLineOfSight(target)
                || !isAhead(player, target, player.getLookAngle().normalize(), radius)) {
            return false;
        }
        boolean hurt = target.hurt(player.damageSources().playerAttack(player), damage);
        if (hurt && knockback > 0.0D) {
            target.knockback(knockback, player.getX() - target.getX(), player.getZ() - target.getZ());
        }
        return hurt;
    }

    public static List<LivingEntity> drainTargets(Player player, double radius, int maximumTargets) {
        AABB area = player.getBoundingBox().inflate(radius);
        List<LivingEntity> candidates = new ArrayList<>(player.level().getEntitiesOfClass(
                LivingEntity.class,
                area,
                candidate -> isValidTarget(player, candidate)
                        && player.distanceToSqr(candidate) <= radius * radius
        ));
        candidates.sort(Comparator.<LivingEntity>comparingDouble(player::distanceToSqr)
                .thenComparing(candidate -> candidate.getUUID().toString()));
        if (candidates.size() > maximumTargets) {
            return List.copyOf(candidates.subList(0, Math.max(0, maximumTargets)));
        }
        return List.copyOf(candidates);
    }

    public static float hurtAndMeasureHealthLoss(ServerPlayer player, LivingEntity target, float damage) {
        if (!isValidTarget(player, target)) {
            return 0.0F;
        }
        float before = target.getHealth();
        if (!target.hurt(player.damageSources().playerAttack(player), damage)) {
            return 0.0F;
        }
        return Math.max(0.0F, before - target.getHealth());
    }

    public static boolean isValidTarget(Player player, LivingEntity target) {
        if (player == null || target == player || !target.isAlive() || target.isRemoved() || target.isAlliedTo(player)) {
            return false;
        }
        if (target instanceof Player targetPlayer) {
            return !targetPlayer.isCreative()
                    && !targetPlayer.isSpectator()
                    && player.canHarmPlayer(targetPlayer);
        }
        if (target instanceof TamableAnimal tamable && tamable.isOwnedBy(player)) {
            return false;
        }
        return !(target instanceof DarkDoppelgangerMinionEntity minion
                && player.getUUID().equals(minion.getSummonerUUID()));
    }

    private static boolean isAhead(Player player, LivingEntity target, Vec3 look, double radius) {
        Vec3 offset = target.getBoundingBox().getCenter().subtract(player.getEyePosition());
        if (offset.lengthSqr() < 1.0E-6D) {
            return true;
        }
        double forward = offset.dot(look);
        if (forward < -radius) {
            return false;
        }
        Vec3 lateral = offset.subtract(look.scale(Math.max(0.0D, forward)));
        return lateral.lengthSqr() <= radius * radius;
    }
}
