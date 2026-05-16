package net.thejadeproject.ascension.refactor_packages.handlers.realm_change;

import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;

public class RealmChangeUtil {
    //TODO there should be a way to simplify this code
    public static void realmChanged(
            IEntityData entityData, ITechnique technique, ITechniqueData data,
            int oldMajorRealm, int oldMinorRealm, int newMajorRealm, int newMinorRealm,
            RealmChangeHandler handler){
        if(oldMajorRealm < newMajorRealm || (oldMajorRealm == newMajorRealm && newMinorRealm>oldMinorRealm)){
            int majorRealmsChanged = newMajorRealm-oldMajorRealm;
            for(int i =newMajorRealm+1;i<=oldMajorRealm;i++){
                handler.dispatch(entityData,data,i,0,RealmChangeType.GAINED);
            }

            if(newMajorRealm != oldMajorRealm) {
                int maxMinorRealm = technique.getMaxMinorRealm(oldMajorRealm);
                for(int i =oldMinorRealm+1;i<=maxMinorRealm;i++){
                    handler.dispatch(entityData,data,oldMajorRealm,i,RealmChangeType.GAINED);
                }
                for (int i = 1; i < majorRealmsChanged; i++) {
                    int majorRealm = oldMajorRealm+i;
                    int minorRealmsForRealm = technique.getMaxMinorRealm(majorRealm);
                    for(int j = 1;j<=minorRealmsForRealm;j++){
                        handler.dispatch(entityData,data,majorRealm,j,RealmChangeType.GAINED);
                    }
                }

                for(int i =1;i<=newMinorRealm;i++){
                    handler.dispatch(entityData,data,newMajorRealm,i,RealmChangeType.GAINED);
                }
            }else{
                for(int i = oldMinorRealm+1;i<=newMinorRealm;i++){
                    handler.dispatch(entityData,data,newMajorRealm,i,RealmChangeType.GAINED);
                }
            }

        }else{
            for(int i = oldMajorRealm; i > newMajorRealm; i--){
                handler.dispatch(entityData,data,i,0,RealmChangeType.LOST);
            }
            if(newMajorRealm != oldMajorRealm){
                for(int i = oldMinorRealm; i > 0; i--){
                    handler.dispatch(entityData,data,oldMajorRealm,i,RealmChangeType.LOST);
                }
                for(int i = oldMajorRealm - 1; i > newMajorRealm; i--){
                    int minorRealmsForRealm = technique.getMaxMinorRealm(i);
                    for(int j = minorRealmsForRealm; j > 0; j--){
                        handler.dispatch(entityData,data,i,j,RealmChangeType.LOST);
                    }
                }
                int minorRealms = technique.getMaxMinorRealm(newMajorRealm);
                for(int i = minorRealms; i > newMinorRealm; i--){
                    handler.dispatch(entityData,data,newMajorRealm,i,RealmChangeType.LOST);
                }
            }else{
                for(int i = oldMinorRealm; i > newMinorRealm; i--){
                    handler.dispatch(entityData,data,newMajorRealm,i,RealmChangeType.LOST);
                }
            }
        }
    }
}
