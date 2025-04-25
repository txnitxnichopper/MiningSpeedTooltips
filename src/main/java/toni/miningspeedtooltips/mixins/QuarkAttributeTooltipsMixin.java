package toni.miningspeedtooltips.mixins;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.Minecraft;

#if FORGE
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.violetmoon.quark.content.client.resources.AttributeSlot;
import org.violetmoon.quark.content.client.tooltip.AttributeTooltips;
import toni.miningspeedtooltips.QuarkCompat;
import org.violetmoon.zeta.client.event.play.ZGatherTooltipComponents;

import java.util.Set;

@Mixin(value = AttributeTooltips.AttributeComponent.class)
#else
@Mixin(value = Minecraft.class)
#endif
public class QuarkAttributeTooltipsMixin {
    #if FORGE
    @Shadow @Final private ItemStack stack;

    @Inject(method = "renderImage", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
    private void onTooltip(Font font, int tooltipX, int tooltipY, GuiGraphics guiGraphics, CallbackInfo ci, @Local(ordinal = 3) int x) {
        QuarkCompat.onTooltip(stack, font, x, tooltipY, guiGraphics);
    }

    @Inject(method = "getWidth", at = @At(value = "RETURN"), cancellable = true)
    private void onTooltip(Font font, CallbackInfoReturnable<Integer> cir) {
        var ret = cir.getReturnValue();
        var val = QuarkCompat.getWidth(font, stack);
        cir.setReturnValue(ret + val);
    }
    #endif
}
