package toni.examplemod.foundation.config;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import toni.examplemod.ExampleMod;
import toni.lib.config.ConfigBase;
import toni.lib.utils.PlatformUtils;
import com.electronwill.nightconfig.core.UnmodifiableConfig;

#if FABRIC
    import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
    #if after_21_1
    import net.neoforged.fml.config.ModConfig;
    import net.neoforged.neoforge.common.ModConfigSpec;
    import net.neoforged.neoforge.common.ModConfigSpec.*;
    #else
    import net.minecraftforge.fml.config.ModConfig;
    import net.minecraftforge.common.ForgeConfigSpec;
    import net.minecraftforge.common.ForgeConfigSpec.*;
    #endif
#endif

#if FORGE
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.fml.config.ModConfig;
#endif

#if NEO
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;
#endif

#if FORGELIKE
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
#endif
public class AllConfigs {

    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    private static CClient client;
    private static CCommon common;
    private static CServer server;

    public static CClient client() {
        return client;
    }

    public static CCommon common() {
        return common;
    }

    public static CServer server() {
        return server;
    }

    public static ConfigBase byType(ModConfig.Type type) {
        return CONFIGS.get(type);
    }

    private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        var specPair = new Builder().configure(builder -> {
            T config = factory.get();
            config.registerAll(builder);
            return config;
        });

        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }

    public static void register(BiConsumer<ModConfig.Type, #if after_21_1 ModConfigSpec #else ForgeConfigSpec #endif> registration) {
        if (!PlatformUtils.isDedicatedServer())
            client = register(CClient::new, ModConfig.Type.CLIENT);
        
        common = register(CCommon::new, ModConfig.Type.COMMON);
        server = register(CServer::new, ModConfig.Type.SERVER);

        for (Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet())
            registration.accept(pair.getKey(), pair.getValue().specification);
    }

    #if FABRIC 
    public static void generateTranslations(FabricLanguageProvider.TranslationBuilder translationBuilder) {
        var existing = new HashSet<String>();

        for (Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet())
        {
            addEntrySetTranslations(existing, pair.getValue().specification.getSpec().entrySet(), translationBuilder);
        }
    }

    public static void addEntrySetTranslations(HashSet<String> existing, Set<? extends UnmodifiableConfig.Entry> config, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        for (var entry : config) {
            if (existing.add(entry.getKey()))
                translationBuilder.add(ExampleMod.ID + ".configuration." + entry.getKey(), entry.getKey());

            if (entry.getValue() instanceof com.electronwill.nightconfig.core.AbstractConfig children) {
                addEntrySetTranslations(existing, children.entrySet(), translationBuilder);
            }
        }
    }
    #endif

    #if FORGELIKE
    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == event.getConfig().getSpec())
                config.onLoad();
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == event.getConfig().getSpec())
                config.onReload();
    }
    #endif
}
