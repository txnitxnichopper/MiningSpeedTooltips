package toni.miningspeedtooltips;

#if FORGE

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.violetmoon.quark.base.QuarkClient;
import org.violetmoon.quark.content.client.module.ImprovedTooltipsModule;
import org.violetmoon.quark.content.client.resources.AttributeIconEntry;

import java.text.DecimalFormat;

import static org.violetmoon.quark.content.client.tooltip.AttributeTooltips.TEXTURE_DOWNGRADE;
import static org.violetmoon.quark.content.client.tooltip.AttributeTooltips.TEXTURE_UPGRADE;

public class QuarkCompat {
    public static void onTooltip(ItemStack itemStack, Font font, int x, int y, GuiGraphics guiGraphics) {
        if (!(itemStack.getItem() instanceof DiggerItem item)) {
            return;
        }

        var valueStr = getTooltipString(item, itemStack);

        guiGraphics.drawString(font, "✳", x, y, -1);

        if (ImprovedTooltipsModule.showUpgradeStatus) {
            var mc = Minecraft.getInstance();
            if (mc.player == null)
                return;

            ItemStack equipped = mc.player.getItemBySlot(EquipmentSlot.MAINHAND);
            if (equipped.getItem() instanceof DiggerItem equippedDigger) {
                double speed = getSpeed(item, itemStack);
                double otherSpeed = getSpeed(equippedDigger, equipped);
                ChatFormatting color = otherSpeed == speed ? ChatFormatting.WHITE : (otherSpeed > speed ? ChatFormatting.RED : ChatFormatting.GREEN);
                if (color != ChatFormatting.WHITE) {
                    int xp = x - 3;
                    int yp = y - 2;
                    if (ImprovedTooltipsModule.animateUpDownArrows && QuarkClient.ticker.total % 20.0F < 10.0F) {
                        ++yp;
                    }

                    guiGraphics.blit(color == ChatFormatting.RED ? TEXTURE_DOWNGRADE : TEXTURE_UPGRADE, xp, yp, 0.0F, 0.0F, 13, 13, 13, 13);
                }

                guiGraphics.drawString(font, Component.literal(valueStr).withStyle(color), x + 10, y, -1);
                return;
            }
        }

        guiGraphics.drawString(font, valueStr, x + 10, y, -1);
    }

    public static int getWidth(Font font, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof DiggerItem item)) {
            return 0;
        }

        var valueStr = getTooltipString(item, itemStack);
        return font.width(valueStr) + 10 + 8;
    }

    private static String getTooltipString(DiggerItem item, ItemStack itemStack) {
        return DecimalFormat.getInstance().format(getSpeed(item, itemStack));
    }

    private static float getSpeed(DiggerItem item, ItemStack itemStack) {
        var speed = item.getTier().getSpeed();
        var level = itemStack.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY);
        if (level > 0)
            speed += (float)(level * level + 1);

        return speed;
    }
}

#endif
