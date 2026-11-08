package com.codechoic3s.blockregexube.client;

import com.codechoic3s.blockregexube.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class TFCForgingTooltipHandler {
    private static final String TFC_FORGING_KEY = "tfc:forging";
    private static final String TARGET_KEY = "target";
    private static final String WORK_KEY = "work";

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!Config.FORGING_TOOLTIP.get()) return;
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;

        // Получаем NBT стака (null если тегов нет)
        CompoundTag rootTag = stack.getTag();
        if (rootTag == null) return;

        // Проверяем наличие Compound с ключом "tfc:forging"
        if (!rootTag.contains(TFC_FORGING_KEY, Tag.TAG_COMPOUND)) return;

        CompoundTag forgingTag = rootTag.getCompound(TFC_FORGING_KEY);

        // Читаем поля (TFC обычно пишет int, но на всякий случай проверяем и long)
        boolean hasTarget = forgingTag.contains(TARGET_KEY, Tag.TAG_INT)
                || forgingTag.contains(TARGET_KEY, Tag.TAG_LONG);
        boolean hasWork   = forgingTag.contains(WORK_KEY, Tag.TAG_INT)
                || forgingTag.contains(WORK_KEY, Tag.TAG_LONG);

        if (hasTarget) {
            int work = 0;
            if (hasWork) {
                work = forgingTag.getInt(WORK_KEY);
            }
            int target = forgingTag.getInt(TARGET_KEY);
            event.getToolTip().add(
                    Component.literal("§aWork: §e" + work + "  " + "§cTarget: §e" + target)
            );
        }
    }
}
