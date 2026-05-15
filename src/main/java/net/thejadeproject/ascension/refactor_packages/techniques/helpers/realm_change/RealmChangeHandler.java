package net.thejadeproject.ascension.refactor_packages.techniques.helpers.realm_change;

import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;

import java.util.HashMap;
import java.util.Set;

/**
 * a generic class to handle realm changes
 *
 * do not try to remove any of these skills on technique removed event
 * this is because when a technique is removed it is only removed for certain realms
 * where listeners are still called
 *
 * same with onAdded, when a fresh technique (0,0) is added dispatch is called for (0,0) so listen to that
 * same with removed i will call dispatch (0,0)
 */
public class RealmChangeHandler {
    //────────────────────────Predicate Builders──────────────────────────
    public static final RealmChangePredicate EVERY_MINOR_REALM = (data) -> data.minorRealm() != 0;
    public static final RealmChangePredicate EVERY_MAJOR_REALM = (data) -> data.minorRealm() == 0;

    public static RealmChangePredicate forAllMinorRealmsInMajorRealms(Set<Integer> majorRealms){
        return (data)->data.minorRealm() != 0 && majorRealms.contains(data.majorRealm());
    }
    public static RealmChangePredicate forEachMajorRealmIn(Set<Integer> majorRealms){
        return ( data)->data.minorRealm()  == 0 && majorRealms.contains(data.majorRealm());
    }
    public static RealmChangePredicate forEachMinorRealmIn(Set<Integer> majorRealms, Set<Integer> minorRealms){
        return ( data)-> majorRealms.contains(data.majorRealm()) && minorRealms.contains(data.minorRealm());
    }
    //────────────────────────Functional interfaces──────────────────────────
    public interface RealmChangePredicate {
        //the test predicate will ONLY run on increments of 1, so a major realm change is detected if minorRealm = 0
        boolean test(RealmChangeData data);
    }
    public interface RealmChangeConsumer {
        void accept(RealmChangeData data);
    }
    //────────────────────────Wrapper──────────────────────────

    private record RealmChangeListener(ResourceLocation id, RealmChangePredicate predicate, RealmChangeConsumer consumer){}

    //────────────────────────Fields──────────────────────────
    private RealmChangeListener[] listeners = new RealmChangeListener[0];


    //────────────────────────Dispatch──────────────────────────

    public void dispatch(IEntityData entityData,ITechniqueData techniqueData,int majorRealm,int minorRealm,RealmChangeType type){
        RealmChangeData data = new RealmChangeData(entityData,techniqueData,majorRealm,minorRealm,type);

        for(RealmChangeListener listener : listeners){
            if(listener.predicate().test(data)) listener.consumer.accept(data);
        }
    }

    //────────────────────────Builder──────────────────────────

    public static Builder from(RealmChangeHandler toCopy){
        RealmChangeHandler handler = new RealmChangeHandler();
        Builder builder = new Builder(handler);
        for (RealmChangeListener listener : toCopy.listeners) {
            builder.listeners.put(listener.id(), listener);
        }

        return builder;
    }
    public static Builder fresh(){
        return new Builder(new RealmChangeHandler());
    }



    public static class Builder{
        private final RealmChangeHandler handler;
        private final HashMap<ResourceLocation,RealmChangeListener> listeners = new HashMap<>();

        private Builder(RealmChangeHandler handler){
            this.handler = handler;
        }

        public Builder addListener(ResourceLocation id, RealmChangePredicate predicate, RealmChangeConsumer consumer){
            listeners.put(id,new RealmChangeListener(id,predicate,consumer));
            return this;
        }
        public Builder changePredicate(ResourceLocation id, RealmChangePredicate predicate){
            if(!listeners.containsKey(id)) return this;
            listeners.put(id,new RealmChangeListener(id,predicate,listeners.get(id).consumer));
            return this;
        }
        public Builder changeConsumer(ResourceLocation id, RealmChangeConsumer consumer){
            if(!listeners.containsKey(id)) return this;
            listeners.put(id,new RealmChangeListener(id,listeners.get(id).predicate,consumer));
            return this;
        }
        public Builder removeListener(ResourceLocation id){
            listeners.remove(id);
            return this;
        }


        public RealmChangeHandler build(){
            handler.listeners = listeners.values().toArray(new RealmChangeListener[0]);
            return handler;
        }
    }
}
