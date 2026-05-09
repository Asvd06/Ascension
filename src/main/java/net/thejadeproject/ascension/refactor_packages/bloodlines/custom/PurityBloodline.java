package net.thejadeproject.ascension.refactor_packages.bloodlines.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.bloodlines.IBloodlineData;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.toast.ShowAscensionToast;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

import static net.thejadeproject.ascension.clients.toast.AscensionToastInterface.DEFAULT_BACKGROUND;

public class PurityBloodline extends GenericBloodline{

    public static final double DEFAULT_PURITY_PER_KILL = 0.01;

    private final ResourceLocation evolvesInto;

    public PurityBloodline(Component title, ResourceLocation evolvesInto) {
        super(title);
        this.evolvesInto = evolvesInto;
    }

    // ─────────────────────────────── data ─────────────────────────────────────


    @Override
    public IBloodlineData freshBloodlineData(IEntityData heldEntity) {
        return new PurityBloodlineData(0);
    }

    @Override
    public IBloodlineData fromCompound(CompoundTag tag, IEntityData heldEntity) {
        return PurityBloodlineData.read(tag);
    }

    @Override
    public IBloodlineData fromNetwork(RegistryFriendlyByteBuf buf) {
        return PurityBloodlineData.decode(buf);
    }

    // ─────────────────────────────── kill hook ────────────────────────────────
    


    @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
          if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
          if (!player.hasData(ModAttachments.ENTITY_DATA)) return;
          IEntityData data = player.getData(ModAttachments.ENTITY_DATA);
         if (data.getBloodline() instanceof PurityBloodline purityBloodline) {
              purityBloodline.onKill(player, data, event.getEntity());
         }
    }
    public void onKill(ServerPlayer player, IEntityData entityData, LivingEntity killed) {
        if (!(entityData.getBloodlineData() instanceof PurityBloodlineData data)) return;
        if (data.isMaxPurity()) return;

        double gain = getPurityPerKill(player, entityData, killed);
        boolean justMaxed = data.addPurity(gain);

        onPurityMilestone(player, entityData, data);

        if (justMaxed) {
            tryEvolve(player, entityData, data);
        }
    }


    // ─────────────────────────────── overrideable ─────────────────────────────

    /**
     * How much purity this kill is worth. Override for mob-type weighting,
     * for example bosses give more, passive animals give none.
     */
    protected double getPurityPerKill(ServerPlayer player, IEntityData entityData, LivingEntity killed) {
        return DEFAULT_PURITY_PER_KILL;
    }

    protected void onPurityMilestone(ServerPlayer player, IEntityData entityData, PurityBloodlineData data) {
        double purity = data.getPurity();

        if (shouldSendMilestoneToast(purity, 25))  sendMilestoneToast(player, "25");
        if (shouldSendMilestoneToast(purity, 50))  sendMilestoneToast(player, "50");
        if (shouldSendMilestoneToast(purity, 75))  sendMilestoneToast(player, "75");
    }

    protected boolean shouldSendMilestoneToast(double purity, double milestone) {
        return purity >= milestone && purity - DEFAULT_PURITY_PER_KILL < milestone;
    }

    // ─────────────────────────────── evolution ────────────────────────────────

    private void tryEvolve(ServerPlayer player, IEntityData entityData, PurityBloodlineData data) {
        if (evolvesInto == null) return;

        var newBloodlineObj = AscensionRegistries.Bloodlines.BLOODLINE_REGISTRY.get(evolvesInto);
        if (newBloodlineObj == null) {
            AscensionCraft.LOGGER.error("PurityBloodline could not find evolution target: " + evolvesInto);
            return;
        }

        entityData.setBloodline(evolvesInto);

        if (player.connection != null) {
            PacketDistributor.sendToPlayer(player, new ShowAscensionToast(
                    newBloodlineObj instanceof PurityBloodline pb
                            ? pb.getDisplayTitle().getString()
                            : evolvesInto.getPath(),
                    "Bloodline Awakened",
                    null,
                    DEFAULT_BACKGROUND
            ));
        }

        broadcastRareAcquired(entityData,
                "ascension.bloodline.awakening_broadcast");
    }

    // ─────────────────────────────── helpers ──────────────────────────────────

    public ResourceLocation getEvolvesInto() {
        return evolvesInto;
    }

    private void sendMilestoneToast(ServerPlayer player, String percent) {
        if (player.connection == null) return;
        PacketDistributor.sendToPlayer(player, new ShowAscensionToast(
                getDisplayTitle().getString(),
                "Bloodline Purity " + percent + "%",
                null,
                DEFAULT_BACKGROUND
        ));
    }
}
