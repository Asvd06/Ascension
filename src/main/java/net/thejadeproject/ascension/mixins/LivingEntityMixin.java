package net.thejadeproject.ascension.mixins;

import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.events.entity.EntitySwingEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Shadow
    public boolean swinging;

    @Inject(method = "getAttributeValue",at=@At("HEAD"),cancellable = true)
    private void getAttributeValue(Holder<Attribute> attributeHolder,CallbackInfoReturnable<Double> cir){
        LivingEntity self = (LivingEntity) (Object) this;

        if(self.hasData(ModAttachments.ENTITY_DATA)){
            IEntityData entityData = self.getData(ModAttachments.ENTITY_DATA);
            if(entityData.getAscensionAttributeHolder().getAttribute(attributeHolder) != null){
                cir.setReturnValue(entityData.getAscensionAttributeHolder().getAttribute(attributeHolder).getValue());
            }
        }
    }
    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V",at=@At(
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/LivingEntity;swinging:Z",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER
    ))
    private void swing(InteractionHand hand,boolean updateSelf,CallbackInfo ci){

        LivingEntity self = (LivingEntity) (Object) this;

        NeoForge.EVENT_BUS.post(new EntitySwingEvent(self,hand));
    }
    @Inject(method = "getMaxAbsorption",at=@At("HEAD"),cancellable = true)
    private void getMaxAbsorption(CallbackInfoReturnable<Float> cir){


        LivingEntity self = (LivingEntity) (Object) this;

        if(self.hasData(ModAttachments.ENTITY_DATA)){
            IEntityData entityData = self.getData(ModAttachments.ENTITY_DATA);
            if(entityData.getAscensionAttributeHolder().getAttribute(Attributes.MAX_ABSORPTION) != null){
                cir.setReturnValue((float)entityData.getAscensionAttributeHolder().getAttribute(Attributes.MAX_ABSORPTION).getValue());
            }
        }
    }

    @Inject(method = "getMaxHealth", at = @At("HEAD"), cancellable = true)
    private void getMaxHealth(CallbackInfoReturnable<Float> cir){
        LivingEntity self = (LivingEntity) (Object) this;

        if(self.hasData(ModAttachments.ENTITY_DATA)){

            cir.setReturnValue((float)self.getData(ModAttachments.ENTITY_DATA).getAscensionAttributeHolder().getAttribute(Attributes.MAX_HEALTH).getValue());
        }
    }

    @Inject(method = "getSpeed", at = @At("HEAD"), cancellable = true)
    private void overrideSpeed(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if(self.hasData(ModAttachments.ENTITY_DATA)){

            cir.setReturnValue((float)self.getData(ModAttachments.ENTITY_DATA).getAscensionAttributeHolder().getAttribute(Attributes.MOVEMENT_SPEED).getValue());
        }

    }}
