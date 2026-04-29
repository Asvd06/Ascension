package net.thejadeproject.ascension.refactor_packages.physiques.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.toast.ShowAscensionToast;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysique;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysiqueData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

import java.util.HashSet;
import java.util.Set;

public class EvolvingPhysique extends GenericPhysique {

    private final Set<ResourceLocation> possibleEvolutions = new HashSet<>();

    public EvolvingPhysique(Component title) {
        super(title);
    }

    public EvolvingPhysique addEvolution(ResourceLocation evolvesInto) {
        possibleEvolutions.add(evolvesInto);
        return this;
    }

    public boolean canEvolveInto(ResourceLocation evolvesInto) {
        return possibleEvolutions.contains(evolvesInto);
    }

    public Set<ResourceLocation> getPossibleEvolutions() {
        return possibleEvolutions;
    }

    public boolean tryEvolveInto(ServerPlayer player, IEntityData entityData, ResourceLocation evolvesInto) {
        if (player == null) return false;
        if (entityData == null) return false;

        if (entityData.getPhysique() != this) return false;

        ResourceLocation currentForm = entityData.getPhysiqueForm();
        if (currentForm == null) return false;

        if (!possibleEvolutions.contains(evolvesInto)) return false;

        if (!meetsEvolutionRequirements(player, entityData, evolvesInto)) return false;

        IPhysique newPhysique = AscensionRegistries.Physiques.PHSIQUES_REGISTRY.get(evolvesInto);
        if (newPhysique == null) return false;

        IPhysiqueData newData = newPhysique.freshPhysiqueData(entityData);

        boolean changed = entityData.setPhysique(evolvesInto, newData, currentForm);

        if (changed) {
            Component physiqueName = newPhysique.getDisplayTitle();

            if (player.connection != null) {
                PacketDistributor.sendToPlayer(
                        player,
                        new ShowAscensionToast(
                                physiqueName.getString(),
                                "Physique Evolved",
                                getEvolutionToastIcon(evolvesInto)
                        )
                );
            }
        }

        return changed;
    }

    protected ItemStack getEvolutionToastIcon(ResourceLocation evolvesInto) {
        return ItemStack.EMPTY;
    }

    // Override in Physique Registration to change evolution conditions
    protected boolean meetsEvolutionRequirements(ServerPlayer player, IEntityData entityData, ResourceLocation evolvesInto) {
        return true;
    }
}