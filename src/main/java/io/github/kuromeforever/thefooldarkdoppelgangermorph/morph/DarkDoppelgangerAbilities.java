package io.github.kuromeforever.thefooldarkdoppelgangermorph.morph;

import com.kurome.ageofmythology.bettermorph.impl.common.MorphSpellCastCoordinator;
import com.kurome.ageofmythology.bettermorph.impl.general.TimeExecutor;
import com.kurome.ageofmythology.model.Ability;
import com.kurome.ageofmythology.model.MorphData;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.TheFoolDarkDoppelgangerMorph;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.mixin.accessor.SummonDoppelMinionSpellInvoker;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.network.DarkDoppelgangerActionSessions;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.runtime.BladeComboService;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.runtime.DarkDoppelgangerSpellSessions;
import io.github.kuromeforever.thefooldarkdoppelgangermorph.runtime.DarkDoppelgangerCombat;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import liushuangwuyin.bettermorph.api.PlayerIdentity;
import net.bandit.darkdoppelganger.entity.DarkDoppelgangerEntity;
import net.bandit.darkdoppelganger.registry.ItemRegistry;
import net.bandit.darkdoppelganger.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class DarkDoppelgangerAbilities {
    public static final int GLOBAL_ABILITY_COOLDOWN_TICKS = 3 * 20;
    public static final int MAX_ACTION_TICKS = 3 * 20;
    public static final int LIFE_DRAIN_DURATION_TICKS = 8;
    public static final int LIFE_DRAIN_HIT_TICK = 4;
    public static final double LIFE_DRAIN_RADIUS = 8.0D;
    public static final int LIFE_DRAIN_MAX_TARGETS = 8;
    public static final float LIFE_DRAIN_DAMAGE = 4.0F;
    public static final float LIFE_DRAIN_MAX_HEAL = 16.0F;

    private DarkDoppelgangerAbilities() {
    }

    public static Ability mirrorBladeCombo() {
        Ability ability = new Ability(
                data("mirror_blade_combo", ItemRegistry.DOPPELGANGER_RING.get()),
                (level, identity, player, executor) -> tickBladeCombo(level, identity, player, executor)
        );
        ability.setAnimationFinishCallbackConsumer((level, identity, player) -> clearBlade(player));
        ability.setAnimationCancelCallbackConsumer((level, identity, player) -> clearBlade(player));
        Predicate<Player> hasTarget = player -> isIdentity(player)
                && DarkDoppelgangerCombat.findNearestAhead(player, 4.0D, 1.0D) != null;
        guard(ability, hasTarget, hasTarget, player -> fail(player, "no_target"));
        return timeline(ability, BladeComboService.TIMELINE_TICKS);
    }

    public static Ability shadowSlash() {
        return sourceSpell(
                "shadow_slash",
                ItemRegistry.SHADOW_ORB.get(),
                io.redspace.ironsspellbooks.api.registry.SpellRegistry.SHADOW_SLASH::get,
                player -> canBeginSpell(player),
                player -> fail(player, "spell_condition")
        );
    }

    public static Ability doppelPortal() {
        return sourceSpell(
                "doppel_portal",
                ItemRegistry.ELDER_NECKLACE.get(),
                net.bandit.darkdoppelganger.registry.SpellRegistry.DOPPEL_PORTAL::get,
                player -> canBeginSpell(player),
                player -> fail(player, "spell_condition")
        );
    }

    public static Ability summonDoppelMinion() {
        Predicate<Player> canSummon = player -> canBeginSpell(player) && !hasLivingMinion(player);
        return sourceSpell(
                "summon_doppel_minion",
                ItemRegistry.SUMMONS_NECKLACE.get(),
                net.bandit.darkdoppelganger.registry.SpellRegistry.MINION_SPELL::get,
                canSummon,
                player -> {
                    if (hasLivingMinion(player)) {
                        fail(player, "already_has_minion");
                    } else {
                        fail(player, "spell_condition");
                    }
                }
        );
    }

    public static Ability lifeDrain() {
        Ability ability = new Ability(
                data("life_drain", ItemRegistry.SHADOW_ORB.get()),
                (level, identity, player, executor) -> {
                    if (!(level instanceof ServerLevel serverLevel)
                            || !(player instanceof ServerPlayer serverPlayer)
                            || !(identity instanceof DarkDoppelgangerEntity)) {
                        return;
                    }
                    if (executor.getAge() == LIFE_DRAIN_HIT_TICK) {
                        drain(serverLevel, serverPlayer);
                    }
                }
        );
        ability.setAnimationFinishCallbackConsumer((level, identity, player) -> stopVisual(player));
        ability.setAnimationCancelCallbackConsumer((level, identity, player) -> stopVisual(player));
        Predicate<Player> hasTargets = player -> isIdentity(player)
                && !DarkDoppelgangerCombat.drainTargets(
                        player, LIFE_DRAIN_RADIUS, LIFE_DRAIN_MAX_TARGETS
                ).isEmpty();
        guard(ability, hasTargets, hasTargets, player -> fail(player, "no_target"));
        return timeline(ability, LIFE_DRAIN_DURATION_TICKS);
    }

    private static void tickBladeCombo(
            net.minecraft.world.level.Level level,
            LivingEntity identity,
            Player player,
            TimeExecutor executor
    ) {
        if (!(level instanceof ServerLevel)
                || !(player instanceof ServerPlayer serverPlayer)
                || !(identity instanceof DarkDoppelgangerEntity)) {
            return;
        }
        if (executor.getAge() == 1) {
            LivingEntity target = DarkDoppelgangerCombat.findNearestAhead(serverPlayer, 4.0D, 1.0D);
            if (!BladeComboService.begin(serverPlayer, target)) {
                fail(serverPlayer, "no_target");
                return;
            }
        }
        BladeComboService.tick(serverPlayer, executor.getAge());
    }

    public static Ability additionalSpell(DarkDoppelgangerSpellCatalog.Entry entry) {
        return sourceSpell(entry.key(), ItemRegistry.SHADOW_ORB.get(), entry::spell,
                DarkDoppelgangerAbilities::canBeginSpell,
                player -> fail(player, "spell_condition"));
    }
    private static Ability sourceSpell(
            String key,
            Item icon,
            Supplier<? extends AbstractSpell> spellSupplier,
            Predicate<Player> activationGuard,
            java.util.function.Consumer<Player> failure
    ) {
        Ability ability = new Ability(data(key, icon), (level, identity, player, executor) -> {
            if (executor.getAge() != 1
                    || !(level instanceof ServerLevel)
                    || !(player instanceof ServerPlayer serverPlayer)
                    || !(identity instanceof DarkDoppelgangerEntity)) {
                return;
            }
            AbstractSpell spell = spellSupplier.get();
            boolean started = DarkDoppelgangerSpellSessions.begin(serverPlayer, spell);
            if (!started) {
                fail(serverPlayer, "action_failed");
            }
        });
        guard(ability, player -> isIdentity(player), player -> activationGuard.test(player)
                && player instanceof ServerPlayer serverPlayer
                && DarkDoppelgangerSpellSessions.canBegin(serverPlayer, spellSupplier.get()), failure);
        return timeline(ability.setNotSpellingMob().setRequiresIronSpellIdle(), 1);
    }

    private static void drain(ServerLevel level, ServerPlayer player) {
        List<LivingEntity> targets = DarkDoppelgangerCombat.drainTargets(
                player, LIFE_DRAIN_RADIUS, LIFE_DRAIN_MAX_TARGETS
        );
        float heal = 0.0F;
        for (LivingEntity target : targets) {
            float lost = DarkDoppelgangerCombat.hurtAndMeasureHealthLoss(player, target, LIFE_DRAIN_DAMAGE);
            heal = Math.min(LIFE_DRAIN_MAX_HEAL, heal + lost);
            if (lost > 0.0F) {
                level.sendParticles(
                        ParticleTypes.SOUL,
                        target.getX(), target.getEyeY(), target.getZ(),
                        6, 0.25D, 0.35D, 0.25D, 0.02D
                );
            }
        }
        if (heal > 0.0F) {
            player.heal(heal);
            level.playSound(
                    null,
                    player.blockPosition(),
                    ModSounds.BOSS_LAUGH.get(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
        }
    }

    private static Ability timeline(Ability ability, int durationTicks) {
        if (durationTicks < 1 || durationTicks > MAX_ACTION_TICKS) {
            throw new IllegalArgumentException("Dark Doppelganger active timeline must be between 1 and 60 ticks");
        }
        return ability
                .setDuration(durationTicks)
                .setTriggerTick(1)
                .setCallBackDelay(durationTicks)
                .setCooldown(GLOBAL_ABILITY_COOLDOWN_TICKS);
    }

    private static Ability guard(
            Ability ability,
            Predicate<Player> clientGuard,
            Predicate<Player> serverGuard,
            java.util.function.Consumer<Player> failure
    ) {
        ability.setClientRequestGuard(clientGuard::test, failure);
        ability.setServerActivationGuard(serverGuard::test, failure);
        return ability;
    }

    private static boolean canBeginSpell(Player player) {
        return isIdentity(player) && MorphSpellCastCoordinator.canActivate(player);
    }

    private static boolean hasLivingMinion(Player player) {
        return player instanceof ServerPlayer serverPlayer
                && SummonDoppelMinionSpellInvoker.thefoolDarkDoppelgangerMorph$hasLivingMinion(
                        serverPlayer.serverLevel(), serverPlayer
                );
    }

    private static boolean isIdentity(Player player) {
        return player != null && PlayerIdentity.getIdentity(player) instanceof DarkDoppelgangerEntity;
    }

    private static MorphData data(String key, Item icon) {
        String translation = "skill." + TheFoolDarkDoppelgangerMorph.MOD_ID + "." + key;
        return new MorphData(
                Component.translatable(translation),
                Component.translatable(translation + ".desc"),
                icon
        );
    }

    private static void clearBlade(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            BladeComboService.clear(serverPlayer);
        }
    }

    private static void stopVisual(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            DarkDoppelgangerActionSessions.stop(serverPlayer);
        }
    }

    private static void fail(Player player, String reason) {
        if (player != null) {
            player.displayClientMessage(
                    Component.translatable("message." + TheFoolDarkDoppelgangerMorph.MOD_ID + "." + reason),
                    true
            );
        }
    }
}
