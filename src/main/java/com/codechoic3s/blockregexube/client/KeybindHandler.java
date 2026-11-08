package com.codechoic3s.blockregexube.client;

import com.codechoic3s.blockregexube.Config;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {
    public static final KeyMapping TOGGLE_ESP = new KeyMapping(
            "key.blockregexesp.toggle",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.blockregexesp"
    );

    public static final KeyMapping OPEN_GUI = new KeyMapping(
            "key.blockregexesp.opengui",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.blockregexesp"
    );

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registrar {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(TOGGLE_ESP);
            event.register(OPEN_GUI);
        }
    }

    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class InputHandler {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            // Toggle ESP — клавиша R
            while (TOGGLE_ESP.consumeClick()) {
                boolean newState = !Config.ENABLED.get();
                Config.ENABLED.set(newState);
                mc.player.displayClientMessage(
                        Component.literal("\u00a76[Block Regex ESP] \u00a7r"
                                + (newState ? "\u00a7aEnabled" : "\u00a7cDisabled")),
                        true
                );
            }

            // Open GUI — клавиша G
            while (OPEN_GUI.consumeClick()) {
                mc.setScreen(new RegexConfigScreen());
            }
        }
    }
//    @SubscribeEvent
//    public static void onKeyInput(InputEvent.Key event) {
//        Minecraft mc = Minecraft.getInstance();
//        if (mc.player == null) return;
//
//        if (TOGGLE_ESP.consumeClick()) {
//            boolean newState = !Config.ENABLED.get();
//            Config.ENABLED.set(newState);
//            mc.player.displayClientMessage(
//                    Component.literal("§6[Block Regex ESP] §r" + (newState ? "§aEnabled" : "§cDisabled")),
//                    true
//            );
//        }
//
//        if (OPEN_GUI.consumeClick()) {
//            mc.setScreen(new RegexConfigScreen());
//        }
//    }
}
