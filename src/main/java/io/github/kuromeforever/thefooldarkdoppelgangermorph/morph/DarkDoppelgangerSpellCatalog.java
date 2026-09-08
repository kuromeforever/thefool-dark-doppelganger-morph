package io.github.kuromeforever.thefooldarkdoppelgangermorph.morph;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Explicit 9.8.2 phase pools and the six current final-phase configuration entries. */
public final class DarkDoppelgangerSpellCatalog {
    public static final List<Entry> ADDITIONS = List.of(
            new Entry("irons_spellbooks:guiding_bolt", "曳光弹", "Guiding Bolt", "向视线方向发射曳光弹，标记命中的目标。"),
            new Entry("irons_spellbooks:blood_needles", "猩红之刺", "Blood Needles", "向视线落点发射一组猩红尖刺。"),
            new Entry("irons_spellbooks:burning_dash", "烈焰冲锋", "Burning Dash", "沿朝向冲锋，以烈焰灼伤沿途目标。"),
            new Entry("irons_spellbooks:blight", "枯萎术", "Blight", "对瞄准的目标施加枯萎。"),
            new Entry("irons_spellbooks:invisibility", "隐身术", "Invisibility", "施法后进入来源隐身状态。"),
            new Entry("irons_spellbooks:charge", "超负荷", "Charge", "以雷电强化自身。"),
            new Entry("irons_spellbooks:heat_surge", "焰涌", "Heat Surge", "释放周身火焰冲击，灼伤附近目标。"),
            new Entry("irons_spellbooks:flaming_strike", "炽焰斩击", "Flaming Strike", "挥出火焰斩击，攻击前方可见目标。"),
            new Entry("irons_spellbooks:frost_step", "霜步", "Frost Step", "向视线方向传送，在原地留下会碎裂的冰霜诱饵。"),
            new Entry("irons_spellbooks:root", "纠缠根须", "Root", "用根须束缚瞄准的目标。"),
            new Entry("irons_spellbooks:thunderstorm", "雷暴", "Thunderstorm", "唤起环绕自身的雷暴，持续攻击附近目标。"),
            new Entry("irons_spellbooks:oakskin", "橡肤", "Oakskin", "以橡木之力强化防护。"),
            new Entry("irons_spellbooks:stomp", "践踏", "Stomp", "踏击地面，释放周身冲击。"),
            new Entry("irons_spellbooks:shockwave", "震荡波", "Shockwave", "蓄力释放周身雷电震荡波。"),
            new Entry("irons_spellbooks:blood_step", "血步", "Blood Step", "闪现至瞄准目标附近，并短暂隐身。"),
            new Entry("irons_spellbooks:evasion", "末影闪避", "Evasion", "获得来源闪避效果，受到攻击时自动闪避。"),
            new Entry("irons_spellbooks:echoing_strikes", "回响打击", "Echoing Strikes", "强化后续攻击，使其产生回响。"),
            new Entry("irons_spellbooks:eldritch_blast", "邪术冲击波", "Eldritch Blast", "向视线落点释放冲击波，并保留来源复施机会。"),
            new Entry("traveloptics:tidal_grasp", "潮汐之扼", "Tidal Grasp", "牵引瞄准的目标，随后拍击并施加来源控制效果。"),
            new Entry("traveloptics:spectral_blink", "幽影闪现", "Spectral Blink", "开启约三秒的来源闪现窗口，按来源提示选择目标并激活。"),
            new Entry("traveloptics:shadowed_miasma", "暗影瘴气", "Shadowed Miasma", "诅咒附近目标，并为后续武器攻击附加瘴气。")
    );
    public static final Set<ResourceLocation> SPELLS = java.util.stream.Stream.concat(
            ADDITIONS.stream().map(Entry::id),
            java.util.stream.Stream.of(ResourceLocation.parse("irons_spellbooks:shadow_slash"),
                    ResourceLocation.parse("darkdoppelganger:doppel_portal"),
                    ResourceLocation.parse("darkdoppelganger:summon_doppel_minion")))
            .collect(Collectors.toUnmodifiableSet());
    private DarkDoppelgangerSpellCatalog() {}
    public static boolean contains(ResourceLocation id) { return SPELLS.contains(id); }
    public record Entry(String sourceId, String chineseName, String englishName, String description) {
        public ResourceLocation id() { return ResourceLocation.parse(sourceId); }
        public String key() { return id().getPath(); }
        public AbstractSpell spell() { return SpellRegistry.getSpell(sourceId); }
    }
}
