package io.github.kuromeforever.thefooldarkdoppelgangermorph;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalizationContractTest {
    @Test
    void sourceChineseCoversAllTwentyKeysAndOnlyApprovedDerivedKeys() throws Exception {
        JsonObject chinese = projectJson("src/main/resources/assets/darkdoppelganger/lang/zh_cn.json");
        try (ZipFile zip = new ZipFile(ContractTestSupport.sourceJar().toFile())) {
            JsonObject english = JsonParser.parseString(ContractTestSupport.zipText(
                    zip, "assets/darkdoppelganger/lang/en_us.json"
            )).getAsJsonObject();
            assertEquals(20, english.size());
            english.keySet().forEach(key -> assertTrue(chinese.has(key), key));
        }
        assertEquals(28, chinese.size());
        Set.of(
                "entity.darkdoppelganger.dark_doppelganger",
                "entity.darkdoppelganger.dark_doppelganger_minion",
                "entity.darkdoppelganger.portal_join_entity",
                "entity.darkdoppelganger.portal_leave_entity",
                "subtitles.darkdoppelganger.boss_fight",
                "subtitles.darkdoppelganger.boss_laugh",
                "subtitles.darkdoppelganger.boss_roar",
                "subtitles.darkdoppelganger.boss_stun"
        ).forEach(key -> assertTrue(chinese.has(key), key));
        chinese.entrySet().forEach(entry -> {
            String value = entry.getValue().getAsString();
            assertFalse(value.isBlank(), entry.getKey());
            assertTrue(value.matches(".*[\\u3400-\\u9FFF].*"), entry.getKey());
        });
    }

    @Test
    void addonLanguagesHaveIdenticalKeysAndCompleteSkillCopy() throws Exception {
        JsonObject english = projectJson(
                "src/main/resources/assets/thefool_dark_doppelganger_morph/lang/en_us.json"
        );
        JsonObject chinese = projectJson(
                "src/main/resources/assets/thefool_dark_doppelganger_morph/lang/zh_cn.json"
        );
        assertEquals(english.keySet(), chinese.keySet());
        assertEquals(26, english.size());
        for (String skill : Set.of(
                "mirror_blade_combo", "shadow_slash", "doppel_portal",
                "summon_doppel_minion", "life_drain"
        )) {
            assertTrue(english.has("skill.thefool_dark_doppelganger_morph." + skill));
            assertTrue(english.has("skill.thefool_dark_doppelganger_morph." + skill + ".desc"));
        }
        chinese.entrySet().forEach(entry -> assertFalse(entry.getValue().getAsString().isBlank(), entry.getKey()));
    }

    @Test
    void soundOverlayPreservesExactSourceSoundsAndAddsSubtitles() throws Exception {
        JsonObject overlay = projectJson("src/main/resources/assets/darkdoppelganger/sounds.json");
        assertEquals(4, overlay.size());
        overlay.entrySet().forEach(entry -> {
            JsonObject definition = entry.getValue().getAsJsonObject();
            assertTrue(definition.get("replace").getAsBoolean(), entry.getKey());
            assertEquals("subtitles.darkdoppelganger." + entry.getKey(), definition.get("subtitle").getAsString());
            assertEquals(1, definition.getAsJsonArray("sounds").size());
            assertEquals(
                    "darkdoppelganger:music/" + entry.getKey(),
                    definition.getAsJsonArray("sounds").get(0).getAsJsonObject().get("name").getAsString()
            );
        });
    }

    @Test
    void literalBridgeIsExactAndDoesNotMutateUnknownText() throws Exception {
        String helper = ContractTestSupport.source(
                "src/main/java/io/github/kuromeforever/thefooldarkdoppelgangermorph/compat/DarkDoppelgangerSourceTranslations.java"
        );
        assertTrue(helper.contains("Unknown strings remain untouched"));
        assertTrue(helper.contains("return Component.literal(original)"));
        assertTrue(helper.contains("startsWith(\"Dark Doppelganger\")"));
        assertTrue(helper.contains("Component.translatable"));
        assertFalse(helper.contains("displayClientMessage"));

        String mixins = ContractTestSupport.source(
                "src/main/resources/thefool_dark_doppelganger_morph.mixins.json"
        );
        assertTrue(mixins.contains("compat.DarkDoppelgangerEntityLocalizationMixin"));
        assertTrue(mixins.contains("compat.DarkDoppelgangerMinionLocalizationMixin"));
        assertTrue(mixins.contains("compat.DarkDoppelgangerCommandLocalizationMixin"));
        assertTrue(mixins.contains("compat.SummonDoppelMinionSpellLocalizationMixin"));
        assertTrue(mixins.contains("compat.SummonScrollLocalizationMixin"));
        assertTrue(mixins.contains("client.DarkDoppelgangerBossBarMixin"));
    }

    private static JsonObject projectJson(String relativePath) throws Exception {
        return JsonParser.parseString(ContractTestSupport.source(relativePath)).getAsJsonObject();
    }
}
