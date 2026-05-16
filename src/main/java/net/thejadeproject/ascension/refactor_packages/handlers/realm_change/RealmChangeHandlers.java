package net.thejadeproject.ascension.refactor_packages.handlers.realm_change;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.stats.Stat;
import net.thejadeproject.ascension.refactor_packages.stats.StatSheet;
import net.thejadeproject.ascension.refactor_packages.stats.custom.ModStats;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ModifierOperation;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ValueContainerModifier;

import java.util.Set;

/**
 * these are just examples and might get removed in the future
 */
public class RealmChangeHandlers {
    //only adds the stat to the base value
    public static RealmChangeHandler.RealmChangeConsumer addStatBonus(Stat stat,double bonus){
        return  (data)-> {
            // +bonus to a stat if we increase our realm
            // -bonus if we decrease our realm
            StatSheet sheet = data.entityData().getEntityFormData(ModForms.MORTAL_VESSEL.get()).getStatSheet();
            if(data.type() == RealmChangeType.GAINED) {
                System.out.println("added "+bonus + " "+stat.getShortName());
                sheet.addStat(stat, bonus);
            }
            else {
                System.out.println("removed "+bonus + " "+stat.getShortName());
                sheet.removeStat(stat, bonus);
            };
        };
    }
    public static RealmChangeHandler.RealmChangeConsumer addStatMultiplier(Stat stat,double bonus){
        return data ->{
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    AscensionCraft.MOD_ID,
                    stat.toString()+"_"+data.majorRealm()+"_"+data.minorRealm()
            );
            ResourceLocation groupId = ResourceLocation.fromNamespaceAndPath(
                    AscensionCraft.MOD_ID,
                    "some_cool_group"
            );


            StatSheet sheet = data.entityData().getEntityFormData(ModForms.MORTAL_VESSEL.get()).getStatSheet();
            if(data.type() == RealmChangeType.GAINED){
                //run when our realm goes up
                ValueContainerModifier modifier = new ValueContainerModifier(bonus, ModifierOperation.MULTIPLY_FINAL,id,groupId);
                sheet.addStatModifier(stat,modifier);
            }else{
                //run when our realm goes down
                sheet.removeStatModifier(stat,id);
            }
        };
    }

    public static RealmChangeHandler.RealmChangeConsumer addSkill(ResourceLocation skill){
        return data ->{

            if(data.type() == RealmChangeType.GAINED){
                //run when our realm goes up
                data.entityData().giveSkill(skill,ModForms.MORTAL_VESSEL.getId());
            }else{
                //run when our realm goes down
                data.entityData().removeSkill(skill, ModForms.MORTAL_VESSEL.getId());
            }
        };
    }

    public static RealmChangeHandler HANDLER_1 = RealmChangeHandler.fresh()
            .addListener(
                    ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"agility_bonus"),
                    RealmChangeHandler.EVERY_MINOR_REALM,
                    addStatBonus(ModStats.AGILITY.get(),5))
            .addListener(
                    ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"agility_multiplier"),
                    RealmChangeHandler.EVERY_MINOR_REALM,
                    addStatMultiplier(ModStats.AGILITY.get(),0.1)
            )
            .addListener(
                    ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"flight"),
                    RealmChangeHandler.forEachMinorRealmIn(Set.of(4),Set.of(3)),
                    addSkill(ModSkills.TRUE_FLIGHT.getId())
            )
            .build();
    public static RealmChangeHandler HANDLER_2 = RealmChangeHandler.from(HANDLER_1)
            .changePredicate(
                    ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"agility_bonus"),
                    RealmChangeHandler.forAllMinorRealmsInMajorRealms(Set.of(0,1,2)))
            .build();



    // minor realm = 0, major realm = 1

    // if type == GAINED means broke through from 0 9 -> 1 0
    // if type == LOST means went down from 1 0 -> 0 9

    // 0 0, gained acquired technique
    // 0 0, lost removed

    public static RealmChangeHandler.RealmChangePredicate ALL_MINOR_REALMS = data-> data.minorRealm() != 0;

    //  3rd 6th 9th minor realm of each major realm
    public static RealmChangeHandler.RealmChangePredicate TEST_PREDICATE = RealmChangeHandler.forEachMinorRealmIn(Set.of(3,6,9));

    //  3rd 6th 9th minor realm only for major realm 4 and 5
    public static RealmChangeHandler.RealmChangePredicate TEST_PREDICATE_2 = RealmChangeHandler.forEachMinorRealmIn(Set.of(4,5),Set.of(3,6,9));



    public static RealmChangeHandler HANDLER_3 = RealmChangeHandler.from(HANDLER_1)
            .addListener(
                    ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "strength_bonus"),
                    TEST_PREDICATE_2,
                    addStatBonus(ModStats.STRENGTH.get(),5)
            )
            .addListener(
                    ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"strength_multiplier"),
                    TEST_PREDICATE,
                    addStatMultiplier(ModStats.STRENGTH.get(),0.2)
                    )
            .changePredicate(
                    ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"agility_bonus"),
                    RealmChangeHandler.EVERY_MAJOR_REALM
            )
            .changeConsumer(
                    ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"agility_bonus"),
                    addStatBonus(ModStats.AGILITY.get(),10)
            )
            .changeConsumer(
                    ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"agility_multiplier"),
                    addStatMultiplier(ModStats.AGILITY.get(),0.2)
            )
            .changePredicate(
                    ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"flight"),
                    RealmChangeHandler.forEachMajorRealmIn(Set.of(2))
            )
            .removeListener(
                    ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"flight")
            )
            .build();
}
