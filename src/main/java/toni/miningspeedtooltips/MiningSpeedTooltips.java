package toni.miningspeedtooltips;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.text.DecimalFormat;

#if FABRIC
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
#endif

#if FORGE
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
#endif


#if NEO
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
#endif

#if mc >= 211
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.Tool;
#endif


#if FORGELIKE
@Mod("miningspeedtooltips")
#endif
public class MiningSpeedTooltips #if FABRIC implements ModInitializer, ClientModInitializer #endif
{
    public static final String MODNAME = "Mining Speed Tooltips";
    public static final String ID = "miningspeedtooltips";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);

    public MiningSpeedTooltips(#if NEO IEventBus modEventBus, ModContainer modContainer #endif) {
        #if FORGE
        var context = FMLJavaModLoadingContext.get();
        var modEventBus = context.getModEventBus();
        #endif

        #if FORGELIKE
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        #endif
    }


    #if FABRIC @Override #endif
    public void onInitialize() {
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, #if mc >= 211 tooltipFlag, #endif tooltip) -> {
            var speed = getTooltip(itemStack);
            if (speed != null)
                tooltip.add(Component.literal(speed).withStyle(ChatFormatting.DARK_GREEN));
        });
    }

    #if FABRIC @Override #endif
    public void onInitializeClient() { }


    #if mc >= 211
    public String getTooltip(ItemStack itemStack) {
        Tool tool = itemStack.get(DataComponents.TOOL);
        if (tool == null)
            return null;

        if (tool.rules().isEmpty())
            return null;

        var speed = 0.0f;
        for (var rule : tool.rules()) {
            if (rule.blocks().unwrapKey().isPresent() && rule.blocks().unwrapKey().get().location().getPath().equals("sword_efficient"))
                return null;

            if (rule.speed().isPresent() && rule.speed().get() > speed)
                speed = rule.speed().get();
        }

        if (speed == 0.0f)
            return null;

        return " " + DecimalFormat.getInstance().format(speed) + " Mining Speed";
    }
    #else
    public String getTooltip(ItemStack itemStack) {
        Material material = itemStack.getItem().getDefaultAttributeModifiers(itemStack);
    }
    #endif

    // Forg event stubs to call the Fabric initialize methods, and set up cloth config screen
    #if FORGELIKE
    public void commonSetup(FMLCommonSetupEvent event) { onInitialize(); }
    public void clientSetup(FMLClientSetupEvent event) { onInitializeClient(); }
    #endif


//
//                ToolMaterial material = tool.getDefaultAttributeModifiers(itemStack);
//                tooltip.add(Component.translatable("tooltip.harvest_level").append(Component.literal(String.valueOf(material.getMiningLevel()))).formatted(Formatting.GRAY));
//                int efficiency = EnchantmentHelper.get(stack).getOrDefault(Enchantments.EFFICIENCY, 0);
//                int efficiencyModifier = efficiency>0?(efficiency*efficiency)+1:0;
//                MutableText speedText = new TranslatableText("tooltip.harvest_speed").append(new LiteralText(String.valueOf(material.getMiningSpeedMultiplier()+efficiencyModifier))).formatted(Formatting.GRAY);
//                if(efficiency > 0) {
//                    speedText.append(new LiteralText(" (+"+efficiencyModifier+")").formatted(Formatting.WHITE));
//                }
}
