package com.codechoic3s.blockregexube;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Blockregexube.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> REGEX_PATTERN;
    public static final ForgeConfigSpec.IntValue SCAN_RADIUS;
    public static final ForgeConfigSpec.IntValue MAX_BLOCKS;
    public static final ForgeConfigSpec.DoubleValue RED;
    public static final ForgeConfigSpec.DoubleValue GREEN;
    public static final ForgeConfigSpec.DoubleValue BLUE;
    public static final ForgeConfigSpec.DoubleValue ALPHA;
    public static final ForgeConfigSpec.DoubleValue LINE_WIDTH;
    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.BooleanValue DEBUG;
    public static final ForgeConfigSpec.BooleanValue RENDER_THROUGH_WALLS;
    public static final ForgeConfigSpec.BooleanValue USE_TRANSLATION_KEY;
    public static final ForgeConfigSpec.BooleanValue CASE_SENSITIVE;
    public static final ForgeConfigSpec.BooleanValue FORGING_TOOLTIP;

    static {
        BUILDER.push("Block Regex ESP");

        ENABLED = BUILDER
                .comment("Enable/disable ESP")
                .define("enabled", true);

        DEBUG = BUILDER
                .comment("Enable/disable Debug")
                .define("debugPlate", false);

        REGEX_PATTERN = BUILDER
                .comment("Regular expression for block filtering. Examples:",
                        "  diamond_ore - exact match",
                        "  .*ore.*   - any ore",
                        "  chest$    - blocks ending with chest",
                        "  ^minecraft:.* - only vanilla blocks")
                .define("regexPattern", ".*ore.*");

        CASE_SENSITIVE = BUILDER
                .comment("Case sensitive matching")
                .define("caseSensitive", false);

        USE_TRANSLATION_KEY = BUILDER
                .comment("Use translated block names instead of IDs")
                .define("useTranslationKey", false);

        SCAN_RADIUS = BUILDER
                .comment("Scan radius in blocks (each direction)")
                .defineInRange("scanRadius", 32, 4, 128);

        MAX_BLOCKS = BUILDER
                .comment("Max displayed blocks (performance)")
                .defineInRange("maxBlocks", 512, 16, 4096);

        RED = BUILDER
                .comment("Red color channel (0.0 - 1.0)")
                .defineInRange("colorRed", 1.0, 0.0, 1.0);

        GREEN = BUILDER
                .comment("Green color channel (0.0 - 1.0)")
                .defineInRange("colorGreen", 0.0, 0.0, 1.0);

        BLUE = BUILDER
                .comment("Blue color channel (0.0 - 1.0)")
                .defineInRange("colorBlue", 0.0, 0.0, 1.0);

        ALPHA = BUILDER
                .comment("Outline alpha (0.0 - 1.0)")
                .defineInRange("alpha", 0.8, 0.0, 1.0);

        LINE_WIDTH = BUILDER
                .comment("Outline line width")
                .defineInRange("lineWidth", 2.0, 0.5, 5.0);

        RENDER_THROUGH_WALLS = BUILDER
                .comment("Render outlines through walls")
                .define("renderThroughWalls", true);

        FORGING_TOOLTIP = BUILDER
                .comment("Enable/disable Forging tooltip")
                .define("forgingTooltip", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
