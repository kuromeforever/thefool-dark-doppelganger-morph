package io.github.kuromeforever.thefooldarkdoppelgangermorph;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThinJarContractTest {
    @Test
    void releaseJarContainsOnlyAdapterOwnedCodeAndApprovedSourceResources() throws Exception {
        List<String> forbidden = List.of(
                "META-INF/jarjar/",
                "com/kurome/ageofmythology/",
                "liushuangwuyin/bettermorph/",
                "io/redspace/ironsspellbooks/",
                "software/bernie/geckolib/",
                "net/bandit/darkdoppelganger/",
                "com/gametechbc/traveloptics/",
                "assets/traveloptics/",
                "dev/architectury/"
        );
        Set<String> approvedSourceResources = Set.of(
                "assets/darkdoppelganger/lang/zh_cn.json",
                "assets/darkdoppelganger/sounds.json"
        );
        Set<String> approvedBuiltInResources = Set.of(
                "builtin/dark_doppelganger_localization/pack.mcmeta",
                "builtin/dark_doppelganger_localization/data/darkdoppelganger/advancements/root.json",
                "builtin/dark_doppelganger_localization/data/darkdoppelganger/advancements/kill.json"
        );
        try (ZipFile zip = new ZipFile(ContractTestSupport.adapterJar().toFile())) {
            List<String> entries = zip.stream()
                    .filter(entry -> !entry.isDirectory())
                    .map(entry -> entry.getName())
                    .toList();
            forbidden.forEach(prefix -> assertTrue(
                    entries.stream().noneMatch(name -> name.startsWith(prefix)), prefix
            ));
            Set<String> actualSourceResources = entries.stream()
                    .filter(name -> name.startsWith("assets/darkdoppelganger/"))
                    .collect(java.util.stream.Collectors.toSet());
            assertEquals(approvedSourceResources, actualSourceResources);
            Set<String> actualBuiltInResources = entries.stream()
                    .filter(name -> name.startsWith("builtin/dark_doppelganger_localization/"))
                    .collect(java.util.stream.Collectors.toSet());
            assertEquals(approvedBuiltInResources, actualBuiltInResources);
            assertTrue(entries.contains("META-INF/mods.toml"));
            String metadata = new String(
                    zip.getInputStream(zip.getEntry("META-INF/mods.toml")).readAllBytes(),
                    StandardCharsets.UTF_8
            );
            assertTrue(metadata.contains("version=\"0.1.4\""));
            assertTrue(metadata.contains("versionRange=\"[0.3.0,0.4.0)\""));
            assertTrue(metadata.contains("modId=\"bettermorph\""));
            assertTrue(metadata.contains("versionRange=\"[0.0.54,)\""));
            assertTrue(metadata.contains("modId=\"traveloptics\""));
            assertTrue(metadata.contains("versionRange=\"[6.3.0-1.20.1,)\""));
            assertTrue(entries.contains("thefool_dark_doppelganger_morph.mixins.json"));
            assertTrue(entries.stream().anyMatch(name -> name.startsWith(
                    "io/github/kuromeforever/thefooldarkdoppelgangermorph/"
            )));
            assertTrue(entries.contains(
                    "io/github/kuromeforever/thefooldarkdoppelgangermorph/compat/"
                            + "DarkDoppelgangerLocalizationPack.class"
            ));
            assertTrue(entries.contains(
                    "io/github/kuromeforever/thefooldarkdoppelgangermorph/mixin/compat/"
                            + "SummonDoppelgangerLocalizationMixin.class"
            ));
        }
    }
}
