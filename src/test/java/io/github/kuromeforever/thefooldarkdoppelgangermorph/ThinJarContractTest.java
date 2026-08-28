package io.github.kuromeforever.thefooldarkdoppelgangermorph;

import org.junit.jupiter.api.Test;

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
                "dev/architectury/"
        );
        Set<String> approvedSourceResources = Set.of(
                "assets/darkdoppelganger/lang/zh_cn.json",
                "assets/darkdoppelganger/sounds.json"
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
            assertTrue(entries.contains("META-INF/mods.toml"));
            assertTrue(entries.contains("thefool_dark_doppelganger_morph.mixins.json"));
            assertTrue(entries.stream().anyMatch(name -> name.startsWith(
                    "io/github/kuromeforever/thefooldarkdoppelgangermorph/"
            )));
        }
    }
}
