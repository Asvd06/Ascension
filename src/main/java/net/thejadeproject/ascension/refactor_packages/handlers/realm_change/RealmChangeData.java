package net.thejadeproject.ascension.refactor_packages.handlers.realm_change;

import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;

public record RealmChangeData(IEntityData entityData, ITechniqueData techniqueData, int majorRealm, int minorRealm,
                              RealmChangeType type){
}
