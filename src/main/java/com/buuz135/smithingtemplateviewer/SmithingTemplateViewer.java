package com.buuz135.smithingtemplateviewer;

import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.level.LevelEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SmithingTemplateViewer.MODID)
public class SmithingTemplateViewer {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "smithingtemplateviewer";
    // Directly reference a slf4j logger

    public SmithingTemplateViewer(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void dimensionChange(LevelEvent.Load event) {
            if (event.getLevel() instanceof ClientLevel clientLevel) {
                SmithingTrimWrapper.INSTANCES.forEach(smithingTrimWrapper -> smithingTrimWrapper.recreateArmorStand(clientLevel));
            }
        }
    }
}
