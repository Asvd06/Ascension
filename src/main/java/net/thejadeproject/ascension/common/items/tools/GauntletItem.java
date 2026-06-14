package net.thejadeproject.ascension.common.items.tools;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;


public class GauntletItem extends TieredItem {
    public GauntletItem(Tier tier, Properties properties) {
        // Intercept properties to inject attack damage and attack speed modifiers
        super(tier, properties.attributes(createAttributes(tier)));
    }
    private static ItemAttributeModifiers createAttributes(Tier tier) {

        // Calculate Damage: Base 2.0 damage + whatever the material tier provides
        float attackDamage = 2.0F + tier.getAttackDamageBonus();

        float attackSpeed = -1.5F;

        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }
}
