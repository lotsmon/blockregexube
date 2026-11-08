package com.codechoic3s.blockregexube.client;

import com.codechoic3s.blockregexube.Config;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Mod.EventBusSubscriber
public class BlockScanner {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static volatile List<BlockPos> matchedBlocks = Collections.emptyList();
    private static Pattern compiledPattern = null;

    private static long lastScanTime = 0;
    private static final int SCAN_INTERVAL_MS = 250;

    private static final ExecutorService scanExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "BlockRegexESP-Scanner");
        t.setDaemon(true);
        return t;
    });

    public static List<BlockPos> getMatchedBlocks() {
        return matchedBlocks;
    }

    private static volatile boolean isScanning = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (!Config.ENABLED.get()) {
            matchedBlocks.clear();
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastScanTime < SCAN_INTERVAL_MS) return;
        lastScanTime = now;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        LocalPlayer player = mc.player;

        if (!player.isAlive()) return;

        if (isScanning) return;

        String regex = Config.REGEX_PATTERN.get();
        final Level level = mc.level;

        BlockPos playerPos = player.blockPosition();
        int radius = Config.SCAN_RADIUS.get();
        int maxBlocks = Config.MAX_BLOCKS.get();
        final boolean useTranslation = Config.USE_TRANSLATION_KEY.get();
        final boolean caseSensitive = Config.CASE_SENSITIVE.get();
        int flags = Config.CASE_SENSITIVE.get() ? 0 : Pattern.CASE_INSENSITIVE;

        final Pattern pattern;
        try {
            if (compiledPattern == null || !compiledPattern.pattern().equals(regex) ||
                    (compiledPattern.flags() & Pattern.CASE_INSENSITIVE) != flags) {
                compiledPattern = Pattern.compile(regex, flags);
            }
            pattern = compiledPattern;
        } catch (PatternSyntaxException e) {
            compiledPattern = null;
            matchedBlocks.clear();
            return;
        }

        if (compiledPattern == null) return;

        isScanning = true;

        scanExecutor.submit(() -> {
            try {
                List<BlockPos> found = new ArrayList<>();

                // Оптимизация: сканируем только загруженные чанки
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            if (found.size() >= maxBlocks) break;

                            BlockPos pos = playerPos.offset(x, y, z);

                            // Критически важно: не вызывать генерацию чанков!
                            if (!level.isLoaded(pos)) continue;

                            BlockState state = level.getBlockState(pos);
                            if (state.isAir()) continue;

                            String blockName;
                            blockName = state.getBlock().asItem().toString();

                            if (pattern.matcher(blockName).find()) {
                                found.add(pos.immutable());
                            }
                        }
                    }
                }

                // Атомарно подменяем список — рендерер получит новые данные мгновенно и безопасно
                matchedBlocks = found;

            } catch (Exception e) {
                // При ошибке не ломаем рендерер
                matchedBlocks.clear();
            } finally {
                isScanning = false;
            }
        });
    }

    public static AABB getBlockBounds(BlockPos pos) {
        return new AABB(pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }
}