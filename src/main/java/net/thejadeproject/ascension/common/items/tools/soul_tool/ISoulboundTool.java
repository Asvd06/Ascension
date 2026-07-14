package net.thejadeproject.ascension.common.items.tools.soul_tool;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.tools.data.soul_tool.SoulToolComponent;

import java.util.List;

public interface ISoulboundTool {

    SoulToolType getSoulToolType();

    default SoulToolComponent getSoulToolComponent(ItemStack stack) {
        return stack.get(ModDataComponents.SOUL_TOOL.get());
    }

    default boolean isFunctioningSoulTool(ItemStack stack) {
        SoulToolComponent component = getSoulToolComponent(stack);

        return component != null
                && component.type() == getSoulToolType()
                && component.core().isFormed();
    }

    default boolean isSoulToolManifestation(ItemStack stack) {
        SoulToolComponent component = getSoulToolComponent(stack);

        return component != null
                && component.type() == getSoulToolType();
    }

    default void appendSoulToolTooltip(
            ItemStack stack,
            List<Component> tooltip
    ) {
        SoulToolComponent component = getSoulToolComponent(stack);

        tooltip.add(
                Component.translatable("item.ascension.soul_tool.tooltip")
                        .withStyle(ChatFormatting.DARK_PURPLE)
        );

        if (component == null) {
            tooltip.add(
                    Component.translatable(
                            "item.ascension.soul_tool.core",
                            Component.translatable(
                                    SoulToolCore.NONE.translationKey()
                            )
                    ).withStyle(ChatFormatting.GRAY)
            );

            return;
        }

        tooltip.add(
                Component.translatable(
                        "item.ascension.soul_tool.form",
                        Component.translatable(
                                component.type().translationKey()
                        )
                ).withStyle(ChatFormatting.GRAY)
        );

        tooltip.add(
                Component.translatable(
                        "item.ascension.soul_tool.core",
                        Component.translatable(
                                component.core().translationKey()
                        )
                ).withStyle(
                        component.core().isFormed()
                                ? ChatFormatting.AQUA
                                : ChatFormatting.DARK_GRAY
                )
        );

        tooltip.add(
                Component.translatable(
                        "item.ascension.soul_tool.harvest",
                        Component.translatable(
                                component.harvest().translationKey()
                        )
                ).withStyle(ChatFormatting.GRAY)
        );

        tooltip.add(
                Component.translatable(
                        "item.ascension.soul_tool.flow",
                        Component.translatable(
                                component.flow().translationKey()
                        )
                ).withStyle(ChatFormatting.GRAY)
        );

        tooltip.add(
                Component.translatable(
                        "item.ascension.soul_tool.spirit",
                        Component.translatable(
                                component.spirit().translationKey()
                        )
                ).withStyle(ChatFormatting.GRAY)
        );
    }
}