package net.thejadeproject.ascension.refactor_packages.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.breakthroughs.IBreakthroughInstance;
import net.thejadeproject.ascension.refactor_packages.breakthroughs.NineHeavenlyTribulations;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.events.CultivateEvent;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;

import java.util.List;

public class CultivationUtil {


    public static boolean tryCultivate(Entity entity, ResourceLocation path,List<ResourceLocation> attributedPaths,double amount){
        IEntityData entityData = entity.getData(ModAttachments.ENTITY_DATA);
        //System.out.println("Player is trying to cultivate");
        IPathData pathData = entity.getData(ModAttachments.ENTITY_DATA).getPathData(path);

        if(pathData == null) return false;
        if(pathData.isBreakingThrough()) return false;

        ITechnique technique = pathData.getCurrentTechnique();

        double base = amount*(entityData.getPathBonusHandler().getPathBonus(path));
        for(ResourceLocation attributedPath : attributedPaths){
            base *= entityData.getPathBonusHandler().getPathBonus(attributedPath);
        }
        CultivateEvent event = new CultivateEvent(entity,base,path, attributedPaths);
        NeoForge.EVENT_BUS.post(event);


        if(pathData.getCurrentRealmProgress()+event.getRate() >= technique.getMaxQiForRealm(pathData.getMajorRealm(),pathData.getMinorRealm())){
            //TODO minor/major realm breakthrough shenanigans here
            pathData.setCurrentRealmProgress(technique.getMaxQiForRealm(pathData.getMajorRealm(),pathData.getMinorRealm()));

            //TODO for now just force breakthrough
            if(pathData.getMinorRealm() < technique.getMaxMinorRealm(pathData.getMajorRealm()) && technique.canBreakthroughMinorRealm(
                    entity.getData(ModAttachments.ENTITY_DATA),
                    pathData.getMajorRealm(),
                    pathData.getMinorRealm(),
                    pathData.getCurrentRealmProgress()
            )){
                pathData.handleRealmChange(pathData.getMajorRealm(),pathData.getMinorRealm()+1,entity.getData(ModAttachments.ENTITY_DATA));
            } else if(pathData.getMajorRealm()<technique.getMaxMajorRealm() && technique.getStabilityHandler() != null) {

                pathData.handleRealmChange(pathData.getMajorRealm()+1,0,entityData);
                //pathData.setCurrentRealmStability(pathData.getCurrentRealmStability()+1);
            }
        }else {
            pathData.setCurrentRealmProgress(pathData.getCurrentRealmProgress()+event.getRate());
        }
        if(entity instanceof ServerPlayer player && player.connection != null) pathData.sync(player);
        return true;
    }
}
