package net.thejadeproject.ascension.refactor_packages.entity_data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.refactor_packages.attributes.AscensionAttributeHolder;
import net.thejadeproject.ascension.refactor_packages.bloodlines.IBloodline;
import net.thejadeproject.ascension.refactor_packages.bloodlines.IBloodlineData;
import net.thejadeproject.ascension.refactor_packages.events.PhysiqueChangeEvent;
import net.thejadeproject.ascension.refactor_packages.forms.IEntityForm;
import net.thejadeproject.ascension.refactor_packages.forms.IEntityFormData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.SyncEntityForm;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.attributes.SyncCurrentHealth;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.path_data.SyncPathData;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.physique.SyncPhysique;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.skills.SyncHeldSkills;
import net.thejadeproject.ascension.refactor_packages.paths.IPath;
import net.thejadeproject.ascension.refactor_packages.paths.PathBonusHandler;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysique;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysiqueData;

import net.thejadeproject.ascension.refactor_packages.physiques.ModPhysiques;
import net.thejadeproject.ascension.refactor_packages.qi.EntityQiContainer;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skill_casting.SkillCastHandler;
import net.thejadeproject.ascension.refactor_packages.skills.*;
import net.thejadeproject.ascension.refactor_packages.skills.tempskills.TemporarySkillHolder;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;

import java.util.*;
//TODO set up some sort of tick handler for entity data and all its parts that need it
public class GenericEntityData implements IEntityData {
    private ResourceLocation activeForm = ModForms.MORTAL_VESSEL.getId();
    private Entity attachedEntity;

    private final SkillCastHandler skillCastHandler = new SkillCastHandler();
    private final HashMap<ResourceLocation, IEntityFormData> heldFormData = new HashMap<>();

    private final HashMap<ResourceLocation,ResourceLocation> pathDataLocation = new HashMap<>();

    private ResourceLocation physiqueForm; //the form holding the data of the physique
    private ResourceLocation bloodlineForm;//almost always body (but might have some rare circumstances where it is not)

    //not final since in the future we will be able to change it
    private AscensionAttributeHolder ascensionAttributeHolder;
    private final PathBonusHandler pathBonusHandler = new PathBonusHandler();
    private final EntityQiContainer entityQiContainer = new EntityQiContainer(this);
    private final TemporarySkillHolder temporarySkills = new TemporarySkillHolder();
    boolean attachedEntityLoaded;


    //used during loading to temporarily store data, and when we save ignore.
    //important because during simulation something might exist before we actually made any data for it
    private HashMap<ResourceLocation,IEntityFormData> cachedFormData = new HashMap<>();
    private HashMap<ResourceLocation, IPersistentSkillData> cachedSkillData = new HashMap<>();

    private double currentHealth = 0;
    private boolean loading = false;

    @Override
    public boolean isLoading() { return loading; }
    //========================== SAVE DATA HANDLING ==========================
    public GenericEntityData(Entity attachedEntity){
        this.attachedEntity = attachedEntity;

        //TODO add mortal vessel
        heldFormData.put(ModForms.MORTAL_VESSEL.getId(),ModForms.MORTAL_VESSEL.get().freshEntityFormData(this));

        //TODO add soul form
        heldFormData.put(ModForms.SOUL_FORM.getId(),ModForms.SOUL_FORM.get().freshEntityFormData(this));

        if(attachedEntity instanceof LivingEntity entity){
            ascensionAttributeHolder = new AscensionAttributeHolder(entity);
            addDefaultAttributes(entity);

        }
        getQiContainer().fullFillQi();
        currentHealth = getAscensionAttributeHolder().getAttribute(Attributes.MAX_HEALTH).getValue();
    }
    //TODO add better error handling so an error does not delete all data
    public GenericEntityData(Entity attachedEntity, CompoundTag tag){
        System.out.println("creating player data");
        this.attachedEntity = attachedEntity;
        //TODO load cached form data
        ListTag formDataTags = tag.getList("form_data", Tag.TAG_COMPOUND);
        ListTag skillDataTags = tag.getList("skill_data",Tag.TAG_COMPOUND);
        ListTag pathDataTags = tag.getList("path_progress",Tag.TAG_COMPOUND);


        for(int i=0;i<formDataTags.size();i++){
            CompoundTag formDataTag = formDataTags.getCompound(i);
            ResourceLocation formId = ResourceLocation.bySeparator(formDataTag.getString("form"),':');
            IEntityForm form = AscensionRegistries.EntityForms.ENTITY_FORMS_REGISTRY.get(formId);
            IEntityFormData formData = form.fromCompound(formDataTag.getCompound("data"),this);
            cachedFormData.put(formId,formData);
        }
        for(int i=0;i<skillDataTags.size();i++){
            CompoundTag skillDataTag = skillDataTags.getCompound(i);
            ResourceLocation skillId = ResourceLocation.bySeparator(skillDataTag.getString("skill"),':');
            ISkill skill = AscensionRegistries.Skills.SKILL_REGISTRY.get(skillId);
            IPersistentSkillData skillData = skill.fromCompound(skillDataTag.getCompound("data"),this);
            cachedSkillData.put(skillId,skillData);
        }

        if(tag.getBoolean("vessel_flag")){

            heldFormData.put(ModForms.MORTAL_VESSEL.getId(),
                    cachedFormData.containsKey(ModForms.MORTAL_VESSEL.getId()) ?
                            cachedFormData.get(ModForms.MORTAL_VESSEL.getId()) : ModForms.MORTAL_VESSEL.get().freshEntityFormData(this));
        }

        heldFormData.put(ModForms.SOUL_FORM.getId(),
                cachedFormData.containsKey(ModForms.SOUL_FORM.getId()) ?
                        cachedFormData.get(ModForms.SOUL_FORM.getId()) : ModForms.SOUL_FORM.get().freshEntityFormData(this));
        if(attachedEntity instanceof LivingEntity entity){
            ascensionAttributeHolder = new AscensionAttributeHolder(entity);
            addDefaultAttributes(entity);

        }

        String rawPhysique = tag.getString("physique");
        //the user has no physique
        if(!rawPhysique.equals("none")){
            ResourceLocation physique = ResourceLocation.bySeparator(rawPhysique,':');
            IPhysique physiqueInstance = AscensionRegistries.Physiques.PHSIQUES_REGISTRY.get(physique);
            IPhysiqueData physiqueData = physiqueInstance.fromCompound(tag.getCompound("physique_data"),this);
            setPhysique(physique,physiqueData);
        }
        String rawBloodline = tag.getString("bloodline");
        //no bloodline
        if(!rawBloodline.equals("none")){
            ResourceLocation bloodline = ResourceLocation.bySeparator(rawBloodline,':');
            IBloodline bloodlineInstance = AscensionRegistries.Bloodlines.BLOODLINE_REGISTRY.get(bloodline);
            IBloodlineData bloodlineData = bloodlineInstance.fromCompound(tag.getCompound("bloodline_data"),this);
            //TODO add to mortal vessel, then run proper physique addition code
        }

        //TODO add cultivation
        loading = true;
        for(int i = 0;i<pathDataTags.size();i++){
            CompoundTag pathDataTag = pathDataTags.getCompound(i);
            ResourceLocation pathId = ResourceLocation.parse(pathDataTag.getString("path"));
            if(pathDataLocation.containsKey(pathId)){
                heldFormData.get(pathDataLocation.get(pathId)).getPathData(pathId).read(pathDataTag.getCompound("data"),this);
            }

            //TODO add a cache for when the form does not yet exist
        }
        loading = false;
        getSkillCastHandler().read(tag.getCompound("skill_cast_handler"));
        getAscensionAttributeHolder().updateAttributes(this);
        getQiContainer().fullFillQi();

        currentHealth = tag.getDouble("current_health");

        if (tag.contains("temporary_skills")) {
            temporarySkills.deserializeNBT(tag.getCompound("temporary_skills"));
        }
    }


    public void sync(Player player){
        for(ResourceLocation form:heldFormData.keySet()){

            PacketDistributor.sendToPlayer((ServerPlayer) player,new SyncEntityForm(heldFormData.get(form)));
        }
    }


    @Override
    public void write(CompoundTag tag) {
        //if the player losses their vessel we do not want to accidentally make a new one
        tag.putBoolean("vessel_flag",heldFormData.containsKey(ModForms.MORTAL_VESSEL.getId()));
        tag.putDouble("current_health",currentHealth);
        tag.putString("physique",physiqueForm == null ? "none" : heldFormData.get(physiqueForm).getPhysiqueKey().toString());
        if (physiqueForm != null && heldFormData.get(physiqueForm).getPhysiqueData() != null) {
            tag.put("physique_data",heldFormData.get(physiqueForm).getPhysiqueData().write());
        }
        tag.putString("bloodline",bloodlineForm == null ? "none" : heldFormData.get(bloodlineForm).getBloodlineKey().toString());
        if (bloodlineForm != null && heldFormData.get(bloodlineForm).getBloodlineData() != null) {
            tag.put("bloodline",heldFormData.get(bloodlineForm).getBloodlineData().write());
        }

        ListTag formTags = new ListTag();
        //since this is only for caching only cache forms that have data
        //for skills keep track of what i have already saved, so i don't accidentally do dupes
        //only write skills that have skill data, otherwise don't
        HashSet<ResourceLocation> visitedSkills = new HashSet<>();
        ListTag skillTags = new ListTag();

        ListTag pathDataTags = new ListTag();
        for(ResourceLocation form : heldFormData.keySet()){
            CompoundTag formTag = new CompoundTag();
            formTag.putString("form",form.toString());
            formTag.put("data",heldFormData.get(form).write());
            formTags.add(formTag);

            HeldSkills skills = heldFormData.get(form).getHeldSkills();
            if(skills != null){
                for(HeldSkill heldSkill : skills.getSkills()){

                    if(heldSkill.getPersistentData() == null) continue;
                    if(visitedSkills.contains(heldSkill.getKey())) continue;

                    visitedSkills.add(heldSkill.getKey());
                    CompoundTag skillTag = new CompoundTag();
                    skillTag.putString("skill",heldSkill.getKey().toString());
                    skillTag.put("data",heldSkill.getPersistentData() == null ? new CompoundTag() :heldSkill.getPersistentData().write());
                    skillTags.add(skillTag);
                }
            }

            for(PathData pathData:heldFormData.get(form).getAllPathData()){
                CompoundTag pathDataTag = new CompoundTag();
                pathDataTag.putString("path",pathData.getPath().toString());
                pathDataTag.put("data",pathData.write());

                pathDataTags.add(pathDataTag);
            }
        }
        tag.put("form_data",formTags);
        tag.put("skill_data",skillTags);
        tag.put("path_progress",pathDataTags);


        tag.put("skill_cast_handler",getSkillCastHandler().write());
        tag.put("temporary_skills", temporarySkills.serializeNBT());
        //path data, make sure to also hold the path
    }

    //========================== FORM DATA HANDLING ==========================
    @Override
    public Entity getAttachedEntity() {
        return attachedEntity;
    }


    public void setAttachedEntity(Entity attachedEntity) {
        this.attachedEntity = attachedEntity;
    }
    @Override
    public IEntityFormData getActiveFormData() {
        return heldFormData.get(activeForm);
    }

    /*
        the way it works
        the form data is STILL ON THE ENTITY WHEN EVERYTHING ELSE IS FIRST REMOVED

        then remove the form and re add all the data
        this way when re adding it to a different entity we do not need to worry about double-dipping

     */
    @Override
    public IEntityFormData removeEntityForm(ResourceLocation form) {
        IEntityFormData removedForm = heldFormData.get(form);
        if(removedForm == null) return  null;
        AscensionRegistries.EntityForms.ENTITY_FORMS_REGISTRY.get(form).onRemoved(this,removedForm);
        if(physiqueForm.equals(form)){
            ResourceLocation oldPhysique = removedForm.getPhysiqueKey();
            IPhysiqueData oldPhysiqueData = removedForm.getPhysiqueData();
            //TODO handle physique removed
            if(form.equals(ModForms.MORTAL_VESSEL)) setPhysique(ModPhysiques.MORTAL.getId(),null,ModForms.SOUL_FORM.getId());
            else setPhysique(ModPhysiques.MORTAL.getId());
            removedForm.setPhysique(oldPhysique,oldPhysiqueData);
        }
        if (bloodlineForm.equals(form)) {
            //TODO
        }
        for(PathData pathData : removedForm.getAllPathData()){
            //TODO handle realm change to 0,0 then remove technique
            pathDataLocation.remove(pathData.getPath());
            pathData.remove(this);
        }
        //TODO trigger onFormRemoved of everything else
        for(Map.Entry<ResourceLocation,IEntityFormData> heldForm : heldFormData.entrySet()){
            IEntityForm heldFormFactory = AscensionRegistries.EntityForms.ENTITY_FORMS_REGISTRY.get(heldForm.getKey());
            IEntityFormData heldFormData = heldForm.getValue();
            heldFormFactory.onFormRemoved(this,heldFormData,removedForm);

            if(heldFormData.getPhysiqueKey() != null){
                heldFormData.getPhysique().onFormRemoved(this,form,heldFormData.getPhysiqueData());
            }
            if(heldFormData.getBloodlineKey() != null){
                heldFormData.getBloodline().onFormRemoved(this,form,heldFormData.getBloodlineData());
            }
            for(PathData pathData : heldFormData.getAllPathData()) pathData.onFormRemoved(this,removedForm);
            heldFormData.getHeldSkills().onFormRemoved(this,form);
        }
        heldFormData.remove(form);
        return removedForm;
    }

    @Override
    public IEntityFormData getEntityFormData(ResourceLocation form) {
        return heldFormData.get(form);
    }

    @Override
    public IEntityFormData getEntityFormData(IEntityForm form) {
        return getEntityFormData(AscensionRegistries.EntityForms.ENTITY_FORMS_REGISTRY.getKey(form));
    }

    @Override
    public Collection<IEntityFormData> getFormData() {
        return heldFormData.values();
    }

    @Override
    public void addEntityForm(ResourceLocation form) {
        IEntityForm formFactory = AscensionRegistries.EntityForms.ENTITY_FORMS_REGISTRY.get(form);
        IEntityFormData formData = formFactory.freshEntityFormData(this);
        addEntityForm(form,formData);

    }

    @Override
    public void addEntityForm(ResourceLocation form, IEntityFormData formData) {
        IEntityForm formFactory = AscensionRegistries.EntityForms.ENTITY_FORMS_REGISTRY.get(form);
        Set<Map.Entry<ResourceLocation,IEntityFormData>> forms = heldFormData.entrySet();
        heldFormData.put(form,formData);
        formFactory.onAdded(this);

        //TODO handle addition of existing data on formData
        //TODO make sure to fully run re add stuff, essentially simulate it


        for(Map.Entry<ResourceLocation,IEntityFormData> heldForm : forms){
            IEntityForm heldFormFactory = AscensionRegistries.EntityForms.ENTITY_FORMS_REGISTRY.get(heldForm.getKey());
            heldFormFactory.onFormAdded(this,heldForm.getValue(),formData);
        }
    }


    @Override
    public void setActiveForm(ResourceLocation activeForm) {
        this.activeForm = activeForm;
        getAscensionAttributeHolder().updateAttributes(this);
    }

    @Override
    public void setFormData(ResourceLocation form, IEntityFormData formData) {
        heldFormData.put(form,formData);
        if(formData.getPhysiqueKey() != null) physiqueForm = form;
        if(formData.getBloodlineKey() != null) bloodlineForm = form;
        for(ResourceLocation path : formData.getPaths()){
            pathDataLocation.put(path,form);
        }
    }

    //============================ PHYSIQUE HANDLING =======================================
    @Override
    public boolean setPhysique(ResourceLocation physique) {
        return setPhysique(physique,AscensionRegistries.Physiques.PHSIQUES_REGISTRY.get(physique).freshPhysiqueData(this));
    }

    @Override
    public boolean setPhysique(ResourceLocation physique, IPhysiqueData existingData) {
        return setPhysique(physique,existingData,ModForms.MORTAL_VESSEL.getId());
    }

    @Override
    public boolean setPhysique(ResourceLocation physique, IPhysiqueData physiqueData,ResourceLocation form) {
        return setPhysique(physique,physiqueData,form,heldFormData.get(physiqueForm));

    }

    public boolean setPhysique(ResourceLocation physique,IPhysiqueData physiqueData,ResourceLocation form,IEntityFormData olPhysiqueForm){
        if(!heldFormData.containsKey(form)) return false;


        ResourceLocation oldPhysique = null;
        IPhysiqueData oldPhysiqueData = null;
        if(physiqueForm != null){
            //no old physique just replace directly
            oldPhysique = olPhysiqueForm.getPhysiqueKey();
            oldPhysiqueData = olPhysiqueForm.getPhysiqueData();

        }
        System.out.println("trying to replace :"+(oldPhysique == null ? "none" : oldPhysique.toString()));

        PhysiqueChangeEvent.Pre preEvent = new PhysiqueChangeEvent.Pre(oldPhysique,oldPhysiqueData,physique,this);
        NeoForge.EVENT_BUS.post(preEvent);
        if(preEvent.isCanceled()) return false;
        physique = preEvent.getNewPhysique();
        if(oldPhysique != null){
            olPhysiqueForm.getPhysique().onPhysiqueRemoved(this,oldPhysiqueData,physique);
            olPhysiqueForm.setPhysique(null);
        }


        physiqueForm = form;

        heldFormData.get(physiqueForm).setPhysique(physique,physiqueData);
        heldFormData.get(physiqueForm).getPhysique().onPhysiqueAdded(this,oldPhysique,oldPhysiqueData);

        for(ResourceLocation path : heldFormData.get(physiqueForm).getPhysique().paths()){

            IPath pathInstance = AscensionRegistries.Paths.PATHS_REGISTRY.get(path);
            PathData pathData = pathInstance.freshPathData(this);
            if(heldFormData.containsKey(pathInstance.defaultForm())){
                addPathData(path,pathData);
            }
        }

        PhysiqueChangeEvent.Post event = new PhysiqueChangeEvent.Post(preEvent,heldFormData.get(physiqueForm).getPhysiqueData());
        System.out.println("changed physique to : "+heldFormData.get(physiqueForm).getPhysique().getDisplayTitle().getString());
        NeoForge.EVENT_BUS.post(event);
        if(getAttachedEntity() instanceof ServerPlayer serverPlayer  && serverPlayer.connection != null)PacketDistributor.sendToPlayer(serverPlayer,new SyncPhysique(physiqueForm,physique,physiqueData));
        return true;
    }

    @Override
    public IPhysiqueData getPhysiqueData() {
        return null; //TODO
    }

    @Override
    public ResourceLocation getPhysiqueForm() {
        return null; //TODO
    }

    @Override
    public IPhysiqueData removePhysique() {
        if(physiqueForm == null) return null;
        if(!heldFormData.containsKey(physiqueForm)) {
            physiqueForm = null;
            return null;
        }

        ResourceLocation oldForm = physiqueForm;
        IEntityFormData formData = heldFormData.get(oldForm);

        ResourceLocation oldPhysique = formData.getPhysiqueKey();
        IPhysiqueData oldPhysiqueData = formData.getPhysiqueData();

        if(oldPhysique == null) {
            physiqueForm = null;
            return null;
        }

        IPhysique physiqueInstance = formData.getPhysique();

        if(physiqueInstance != null) {
            physiqueInstance.onPhysiqueRemoved(this, oldPhysiqueData, null);

            for(ResourceLocation path : new HashSet<>(physiqueInstance.paths())) {
                removePath(path);
            }
        }

        formData.setPhysique(null);
        physiqueForm = null;

        if(getAttachedEntity() instanceof ServerPlayer serverPlayer && serverPlayer.connection != null) {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncEntityForm(formData));
        }

        return oldPhysiqueData;
    }

    @Override
    public IPhysique getPhysique() {
        if (physiqueForm == null) return null;
        IEntityFormData formData = heldFormData.get(physiqueForm);
        if (formData == null) return null;
        return formData.getPhysique();
    }

    @Override
    public void movePhysique(ResourceLocation form) {
        if(!heldFormData.containsKey(form))return;
        ResourceLocation physique = heldFormData.get(physiqueForm).getPhysiqueKey();
        IPhysiqueData physiqueData = heldFormData.get(physique).getPhysiqueData();
        heldFormData.get(physiqueForm).setPhysique(null);
        heldFormData.get(form).setPhysique(physique,physiqueData);
        physiqueForm = form;
    }

    //============================ BLOODLINE HANDLING =======================================
    @Override
    public void setBloodline(ResourceLocation bloodline) {
        //TODO
    }

    @Override
    public void setBloodline(ResourceLocation bloodline, IBloodlineData existingData) {
        //TODO
    }

    @Override
    public IBloodlineData getBloodlineData() {
        return null;//TODO
    }

    @Override
    public ResourceLocation getBloodlineForm() {
        return null;//TODO
    }

    @Override
    public IBloodlineData removeBloodline() {
        return null;//TODO
    }

    @Override
    public IBloodline getBloodline() {
        return heldFormData.get(bloodlineForm).getBloodline();
    }

    @Override
    public void moveBloodline(ResourceLocation form) {

    }

    //============================ CULTIVATION DATA HANDLING ==================================
    @Override
    public boolean hasPath(ResourceLocation path) {
        if(!pathDataLocation.containsKey(path)) return false;

        ResourceLocation form = pathDataLocation.get(path);
        if(!heldFormData.containsKey(form)) return false;

        return heldFormData.get(form).hasPathData(path);
    }

    @Override
    public boolean isCultivating() {
        return false;//TODO
    }

    @Override
    public boolean isCultivating(ResourceLocation path) {
        return false;//TODO
    }

    @Override
    public ResourceLocation getTechnique(ResourceLocation path) {
        PathData pathData = getPathData(path);
        if(pathData == null) return null;

        ResourceLocation technique = pathData.getLastUsedTechnique();
        if(technique == null || technique.toString().equals("ascension:none")) return null;

        return technique;
    }

    @Override
    public ITechniqueData getTechniqueData(ResourceLocation path) {
        PathData pathData = getPathData(path);
        if(pathData == null) return null;

        ResourceLocation technique = pathData.getLastUsedTechnique();
        if(technique == null || technique.toString().equals("ascension:none")) return null;

        return pathData.getTechniqueData(technique);
    }

    @Override
    public PathData getPathData(ResourceLocation path) {
        if(!pathDataLocation.containsKey(path)) return null;
        return heldFormData.get(pathDataLocation.get(path)).getPathData(path);//TODO
    }

    @Override
    public Collection<ResourceLocation> getPathDataForms(ResourceLocation path) {
        ArrayList<ResourceLocation> forms = new ArrayList<>();
        for(ResourceLocation form : heldFormData.keySet()){
            if(heldFormData.get(form).hasPathData(path)) forms.add(form);
        }
        return forms;
    }

    @Override
    public Collection<PathData> getAllPathData() {
        HashSet<PathData> data = new HashSet<>();
        for(IEntityFormData formData : heldFormData.values()){
            data.addAll(formData.getAllPathData());
        }
        return data;
    }

    @Override
    public ITechniqueData removeTechnique(ResourceLocation path) {
        if(!pathDataLocation.containsKey(path)) return null;

        ResourceLocation form = pathDataLocation.get(path);
        if(!heldFormData.containsKey(form)) return null;

        PathData pathData = heldFormData.get(form).getPathData(path);
        if(pathData == null) return null;

        ResourceLocation oldTechnique = pathData.getLastUsedTechnique();
        if(oldTechnique == null || oldTechnique.toString().equals("ascension:none")) return null;

        ITechniqueData oldTechniqueData = pathData.getTechniqueData(oldTechnique);
        ITechnique oldTechniqueInstance = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(oldTechnique);

        if(oldTechniqueInstance != null) {
            oldTechniqueInstance.onTechniqueRemoved(this, oldTechniqueData);
        }

        pathData.removeLastUsedTechnique();

        if(getAttachedEntity() instanceof ServerPlayer serverPlayer && serverPlayer.connection != null) {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncPathData(form, pathData));
        }

        return oldTechniqueData;
    }

    @Override
    public void setPathForm(ResourceLocation path, ResourceLocation form) {
        if(!pathDataLocation.containsKey(path) || !heldFormData.containsKey(form)) return;

        pathDataLocation.put(path,form);
    }

    @Override
    public boolean setTechnique(ResourceLocation technique) {
        return setTechnique(technique,AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique).freshTechniqueData(this));
    }

    @Override
    public boolean setTechnique(ResourceLocation technique, ITechniqueData techniqueData) {
        System.out.println("trying to set technique");
        ITechnique techniqueInstance = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique);
        ResourceLocation path = techniqueInstance.getPath();
        if(!pathDataLocation.containsKey(path)) return false;
        PathData pathData = heldFormData.get(pathDataLocation.get(path)).getPathData(path);
        if(pathData == null) return false;
        ITechnique oldTechnique = null;
        if(pathData.getLastUsedTechnique() != null){

            if(technique.equals(pathData.getLastUsedTechnique())){
                return false; //we have already learned this technique
            }
            oldTechnique = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(pathData.getLastUsedTechnique());
            oldTechnique.onTechniqueRemoved(this,pathData.getTechniqueData(pathData.getLastUsedTechnique()));
        }

        //TODO then check compatibility with technique history, if even 1 is not compatible we reset cultivation data
        if(oldTechnique != null){
            //there was a previous technique so check for compatibility
            pathData.removeLastUsedTechnique();
            for(ResourceLocation usedTechnique : pathData.getTechniqueHistory()){
                if(techniqueInstance.isCompatibleWith(usedTechnique)) continue;

                pathData.resetCultivation();

                break;
            }

        }
        pathData.setLastUsedTechnique(technique);
        pathData.addTechniqueData(technique,techniqueData);

        techniqueInstance.onTechniqueAdded(this);
        System.out.println("technique changed to: "+technique.toString());
        if(getAttachedEntity() instanceof ServerPlayer serverPlayer  && serverPlayer.connection != null){
            PacketDistributor.sendToPlayer(serverPlayer,new SyncPathData(pathDataLocation.get(path),pathData));
        }
        return true;
    }

    /*TODO need to consider a circumstance where the player said merged essence onto spirit then lost mortal vessel
        in this situation when reloading they would lose said cultivation, so i should create a "cached" version of the form data?
        and then when the player tries to move it later on we check if the form is in the cached form data?
        should work.
        basically when player calls getPathData(). check if we have any cached path data
        and when we do move pathData to form we check if that path is cached, if so we use that

     */


    @Override
    public void addPathData(ResourceLocation path, PathData pathData) {
        if(pathDataLocation.containsKey(path)) return;


        IPath pathInstance = AscensionRegistries.Paths.PATHS_REGISTRY.get(path);
        if(!heldFormData.containsKey(pathInstance.defaultForm())) return;

        pathDataLocation.put(path,pathInstance.defaultForm());
        heldFormData.get(pathInstance.defaultForm()).addPathData(path,pathData);
        if(pathData.getLastUsedTechnique() != null && !pathData.getLastUsedTechnique().toString().equals("ascension:none")){
            AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(pathData.getLastUsedTechnique()).onTechniqueAdded(this);
        }
        if(getAttachedEntity() instanceof ServerPlayer serverPlayer  && serverPlayer.connection != null){
            PacketDistributor.sendToPlayer(serverPlayer,new SyncPathData(pathDataLocation.get(path),pathData));
        }
    }

    @Override
    public void removePath(ResourceLocation path) {
        if(!pathDataLocation.containsKey(path)) return;

        ResourceLocation form = pathDataLocation.get(path);

        if(!heldFormData.containsKey(form)) {
            pathDataLocation.remove(path);
            return;
        }

        IEntityFormData formData = heldFormData.get(form);
        PathData pathData = formData.getPathData(path);

        if(pathData == null) {
            pathDataLocation.remove(path);
            return;
        }

        removeTechnique(path);

        pathData.remove(this);
        formData.removePathData(path);
        pathDataLocation.remove(path);

        if(getAttachedEntity() instanceof ServerPlayer serverPlayer && serverPlayer.connection != null) {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncEntityForm(formData));
        }
    }

    @Override
    public PathBonusHandler getPathBonusHandler() {
        return pathBonusHandler;
    }
    //============================ BREAKTHROUGH HANDLING ==================================


    @Override
    public boolean isBreakingThrough(ResourceLocation path) {
        if(!pathDataLocation.containsKey(path)) return false;
        if(!heldFormData.containsKey(pathDataLocation.get(path))) return false;

        PathData data = heldFormData.get(pathDataLocation.get(path)).getPathData(path);
        if(data == null) return false;
        return data.isBreakingThrough();
    }

    //============================ SKILL DATA HANDLING ==================================
    @Override
    public void giveSkill(ResourceLocation skill, ResourceLocation form) {
        IPersistentSkillData skillData = AscensionRegistries.Skills.SKILL_REGISTRY.get(skill).freshPersistentData(this);
        giveSkill(skill,skillData,form);
    }

    @Override
    public void giveSkill(ResourceLocation skill, IPersistentSkillData skillData, ResourceLocation form) {

        IPersistentSkillData existingSkillData = getSkillData(skill);
        if(existingSkillData != null) skillData =existingSkillData;

        if(!heldFormData.containsKey(form)) return;

        HeldSkills skills = heldFormData.get(form).getHeldSkills();
        if(skills.hasSkill(skill)) return;


        boolean hasSkill = hasSkill(skill);
        skills.addSkill(skill,skillData);
        if(!hasSkill){
            ISkill skillInstance = AscensionRegistries.Skills.SKILL_REGISTRY.get(skill);
            skillInstance.onAdded(this);
        }
        //TODO update to sync only changes
        if(getAttachedEntity() instanceof ServerPlayer serverPlayer && serverPlayer.connection != null){
            PacketDistributor.sendToPlayer(serverPlayer,new SyncHeldSkills(form.toString(),heldFormData.get(form).getHeldSkills()));
        }
    }

    @Override
    public void removeSkill(ResourceLocation skill, ResourceLocation form) {
        if(!hasSkill(skill)) return;
        if(!heldFormData.containsKey(form)) return;
        if(!heldFormData.get(form).getHeldSkills().hasSkill(skill)) return;

        IPersistentSkillData skillData = heldFormData.get(form).getHeldSkills().removeSkill(skill);
        if(hasSkill(skill)) return;

        ISkill skillInstance = AscensionRegistries.Skills.SKILL_REGISTRY.get(skill);
        skillInstance.onRemoved(this,skillData);
        //TODO update to sync only changes
        if(getAttachedEntity() instanceof ServerPlayer serverPlayer && serverPlayer.connection != null){
            PacketDistributor.sendToPlayer(serverPlayer,new SyncHeldSkills(form.toString(),heldFormData.get(form).getHeldSkills()));
        }
    }

    @Override
    public boolean hasSkill(ResourceLocation skill) {
        for(IEntityFormData formData : heldFormData.values()){
            if(formData.getHeldSkills() != null && formData.getHeldSkills().hasSkill(skill)) return true;
        }
        return false;
    }

    @Override
    public IPersistentSkillData getSkillData(ResourceLocation skill) {
        if(cachedSkillData.containsKey(skill)) return cachedSkillData.remove(skill);
        for(IEntityFormData formData : heldFormData.values()){
            if(formData.getHeldSkills().getHeldSkill(skill) != null){
                return formData.getHeldSkills().getHeldSkill(skill).getPersistentData();
            }
        }
        return null;
    }

    @Override
    public Set<ResourceLocation> getAllSkills() {
        HashSet<ResourceLocation> skills = new HashSet<>();

        for(IEntityFormData formData : heldFormData.values()){
            for(HeldSkill heldSkill : formData.getHeldSkills().getSkills()){
                skills.add(heldSkill.getKey());
            }
        }
        return skills;
    }

    @Override
    public TemporarySkillHolder getTemporarySkills() {
        return temporarySkills;
    }

    //============================= SKILL CASTING ====================================
    @Override
    public SkillCastHandler getSkillCastHandler() {
        return skillCastHandler;
    }

    @Override
    public EntityQiContainer getQiContainer() {
        return entityQiContainer;
    }

    @Override
    public void tick() {
        if (((LivingEntity) attachedEntity).tickCount % 20 == 0) {
            entityQiContainer.tryRegenQi();
        }
    }

    //============================= ATTRIBUTES =======================================
    @Override
    public AscensionAttributeHolder getAscensionAttributeHolder() {
        return ascensionAttributeHolder;
    }

    @Override
    public void setAscensionAttributeHolder(LivingEntity entity,AscensionAttributeHolder holder) {
        this.ascensionAttributeHolder = holder;
        if(entity != null && holder != null) holder.setAttachedEntity(entity);
    }

    /*
        should mainly be used for healing, instances of damage should include a source
     */
    @Override
    public void setHealth(double val) {
        this.currentHealth = val;
        if(currentHealth <= 0 && getAttachedEntity() != null && getAttachedEntity() instanceof LivingEntity entity) {

            if(val != 0) entity.setHealth(0);
            currentHealth = 0;
        }
        if(getAttachedEntity() instanceof  ServerPlayer serverPlayer && serverPlayer.connection != null){
            PacketDistributor.sendToPlayer(serverPlayer,new SyncCurrentHealth(currentHealth));

        }
    }

    @Override
    public void setHealth(double val, DamageSource source) {
        this.currentHealth = val;
        if(currentHealth <= 0 && getAttachedEntity() != null && getAttachedEntity() instanceof  LivingEntity entity) {
            if(val != 0) entity.setHealth(0);
            currentHealth = 0;
            //entity.die(source);


        }
        if(getAttachedEntity() instanceof  ServerPlayer serverPlayer && serverPlayer.connection != null){
            PacketDistributor.sendToPlayer(serverPlayer,new SyncCurrentHealth(currentHealth));

        }
    }

    @Override
    public double getHealth() {
        return this.currentHealth;
    }

}