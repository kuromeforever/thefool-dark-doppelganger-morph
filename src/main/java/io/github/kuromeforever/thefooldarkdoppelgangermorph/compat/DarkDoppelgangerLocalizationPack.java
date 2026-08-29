package io.github.kuromeforever.thefooldarkdoppelgangermorph.compat;

import io.github.kuromeforever.thefooldarkdoppelgangermorph.TheFoolDarkDoppelgangerMorph;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.resource.PathPackResources;

import java.nio.file.Path;

/** Owns the two exact 9.8.2 advancement display overrides at an explicit server-data priority. */
@Mod.EventBusSubscriber(
        modid = TheFoolDarkDoppelgangerMorph.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class DarkDoppelgangerLocalizationPack {
    static final String PACK_ID = "builtin/thefool_dark_doppelganger_morph_advancements";
    static final String PACK_PATH = "builtin/dark_doppelganger_localization";
    static final String PACK_TITLE_KEY = "pack.thefool_dark_doppelganger_morph.advancement_overrides";

    private DarkDoppelgangerLocalizationPack() {
    }

    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.SERVER_DATA) {
            return;
        }

        var modFile = ModList.get().getModFileById(TheFoolDarkDoppelgangerMorph.MOD_ID).getFile();
        Path packRoot = modFile.findResource(PACK_PATH);
        event.addRepositorySource(consumer -> {
            Pack pack = Pack.readMetaAndCreate(
                    PACK_ID,
                    Component.translatable(PACK_TITLE_KEY),
                    true,
                    ignored -> new PathPackResources(modFile.getFileName() + ":" + PACK_PATH, true, packRoot),
                    PackType.SERVER_DATA,
                    Pack.Position.TOP,
                    PackSource.BUILT_IN
            );
            if (pack == null) {
                throw new IllegalStateException("Missing built-in advancement localization pack metadata");
            }
            consumer.accept(pack);
        });
    }
}
