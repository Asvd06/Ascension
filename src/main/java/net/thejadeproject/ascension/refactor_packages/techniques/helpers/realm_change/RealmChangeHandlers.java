package net.thejadeproject.ascension.refactor_packages.techniques.helpers.realm_change;

import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.forms.IEntityFormData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.stats.Stat;
import net.thejadeproject.ascension.refactor_packages.stats.StatSheet;
import net.thejadeproject.ascension.refactor_packages.stats.custom.ModStats;

import java.util.Set;

/**
 * these are just examples and might get removed in the future
 */
public class RealmChangeHandlers {
    //only adds the stat to the base value
    public static RealmChangeHandler.RealmChangeConsumer addStatBonus(Stat stat,double bonus){
        return  (data)-> {
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
    public static RealmChangeHandler HANDLER_1 = RealmChangeHandler.fresh()
            .addListener(
                    ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"agility_bonus"),
                    RealmChangeHandler.EVERY_MINOR_REALM,
                    addStatBonus(ModStats.AGILITY.get(),5))
            .build();
    public static RealmChangeHandler HANDLER_2 = RealmChangeHandler.from(HANDLER_1)
            .changePredicate(
                    ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"agility_bonus"),
                    RealmChangeHandler.forAllMinorRealmsInMajorRealms(Set.of(0,1,2)))
            .build();
}
