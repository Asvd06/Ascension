package net.thejadeproject.ascension;


import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.ModItems;
import net.thejadeproject.ascension.common.items.techniques.TechniqueTransferItem;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

public class CreativeTabHandler {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AscensionCraft.MOD_ID);


    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);

        CREATIVE_TABS.register("technique_transfers",()->CreativeModeTab.builder()
                .title(Component.literal("Ascension - Technique Manuals"))
                .icon(()->{
                    return TechniqueTransferItem.createWithTechnique("ascension:none");
                })
                .displayItems(((itemDisplayParameters, output) -> {
                    generateTechniqueTransferItem(output);
                }))
                .build()
        );
        // Register a creative tab for physique transfer items
        CREATIVE_TABS.register("physique_transfers", () -> CreativeModeTab.builder()
                .title(Component.literal("Ascension - Physique Essences"))
                .icon(() -> {
                    // Create a default item stack for the tab icon
                    ItemStack stack = new ItemStack(ModItems.PHYSIQUE_ESSENCE.get());
                    // Set it to the first physique found (or empty vessel as fallback)
                    String firstPhysiqueId = "ascension:empty_vessel";

                    // Try to find a more interesting physique for the icon
                    for (var entry : AscensionRegistries.Physiques.PHSIQUES_REGISTRY.entrySet()) {
                        if (!entry.getKey().toString().equals("ascension:empty_vessel")) {
                            firstPhysiqueId = entry.getKey().toString();
                            break;
                        }
                    }

                    stack.set(ModDataComponents.PHYSIQUE_ID.get(), firstPhysiqueId);
                    stack.set(ModDataComponents.PURITY.get(), 100); // Full purity for the icon
                    return stack;
                })
                .displayItems((parameters, output) -> {
                    generatePhysiqueTransferItems(output);
                })
                .build());

    }

    private static void generateTechniqueTransferItem(CreativeModeTab.Output output){
        AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.keySet().forEach(resourceLocation -> {
            output.accept(TechniqueTransferItem.createWithTechnique(resourceLocation.toString()));
        });
    }

    private static void generatePhysiqueTransferItems(CreativeModeTab.Output output) {


        AscensionRegistries.Physiques.PHSIQUES_REGISTRY.keySet().forEach(resourceLocation -> {
            String id = resourceLocation.toString();

//            ItemStack stack = new ItemStack(ModItems.PHYSIQUE_ESSENCE.get());
//            stack.set(ModDataComponents.PHYSIQUE_ID.get(), id);
//            stack.set(ModDataComponents.PURITY.get(), 1); // Start with 1% purity
//            output.accept(stack);

            // Also add a 100% purity version for testing
            ItemStack fullStack = new ItemStack(ModItems.PHYSIQUE_ESSENCE.get());
            fullStack.set(ModDataComponents.PHYSIQUE_ID.get(), id);
            fullStack.set(ModDataComponents.PURITY.get(), 100);
            output.accept(fullStack);

        });


    }


}