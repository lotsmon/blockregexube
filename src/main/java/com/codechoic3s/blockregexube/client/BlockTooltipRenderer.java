package com.codechoic3s.blockregexube.client;

import com.codechoic3s.blockregexube.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Отображает имя блока (по которому идёт поиск regex) при наведении курсора.
 * Работает только на клиенте, не требует сервер.
 */
@Mod.EventBusSubscriber(Dist.CLIENT)
public class BlockTooltipRenderer {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (!Config.DEBUG.get()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Получаем объект, на который смотрит кроссхейр игрока
        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult blockHit)) return;

        BlockState state = mc.level.getBlockState(blockHit.getBlockPos());
        if (state.isAir()) return;

        // Формируем имя блока точно так же, как в BlockScanner
        String searchName;
        String displayName; // Что показываем на экране

        searchName = state.getBlock().asItem().toString();
        displayName = searchName;

        // Проверяем, совпадает ли блок с текущим regex
        boolean isMatch = false;
        String regex = Config.REGEX_PATTERN.get();
        if (!regex.isEmpty()) {
            try {
                int flags = Config.CASE_SENSITIVE.get() ? 0 : Pattern.CASE_INSENSITIVE;
                Pattern pattern = Pattern.compile(regex, flags);
                isMatch = pattern.matcher(searchName).find();
            } catch (PatternSyntaxException ignored) {
                // Невалидный regex — считаем NO MATCH
            }
        }

        // --- Рендер текста на экране ---
        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // Цвета: зелёный если совпадает, красный если нет, жёлтый если regex невалиден
        String matchStatus = isMatch ? "\u00a7a[MATCH]" : "\u00a7c[NO MATCH]";
        String regexText = "\u00a77Regex: \u00a7f" + regex;

        // Строка 1: имя блока
        String line1 = "\u00a7eBlock: \u00a7f" + displayName;
        // Строка 2: статус совпадения
        String line2 = matchStatus;
        // Строка 3: текущий паттерн (сокращённо, если длинный)
        String line3 = regex.length() > 30
                ? "\u00a77Pattern: \u00a7f" + regex.substring(0, 27) + "..."
                : regexText;

        int x = screenWidth / 2 + 15;  // Справа от кроссхейра
        int y = screenHeight / 2 - 10; // По центру высоты

        // Фон для читаемости (полупрозрачный чёрный прямоугольник)
        int maxWidth = Math.max(
                mc.font.width(line1),
                Math.max(mc.font.width(line2), mc.font.width(line3))
        );
        guiGraphics.fill(x - 2, y - 2, x + maxWidth + 4, y + 30, 0xAA000000);

        // Рисуем строки
        guiGraphics.drawString(mc.font, line1, x, y, 0xFFFFFF);
        guiGraphics.drawString(mc.font, line2, x, y + 10, 0xFFFFFF);
        guiGraphics.drawString(mc.font, line3, x, y + 20, 0xFFFFFF);
    }
}