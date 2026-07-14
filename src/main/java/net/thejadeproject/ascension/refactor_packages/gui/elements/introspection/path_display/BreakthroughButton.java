package net.thejadeproject.ascension.refactor_packages.gui.elements.introspection.path_display;

import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.elements.built_in.EasyLabel;
import net.lucent.easygui.gui.textures.ITextureData;
import net.lucent.easygui.gui.textures.TextureDataSubsection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.gui.elements.general.BetterButton;
import net.thejadeproject.ascension.refactor_packages.network.server_bound.StartTribulationPayload;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;

public class BreakthroughButton extends BetterButton {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    AscensionCraft.MOD_ID,
                    "textures/gui/main/path_menu/path_text_buttons.png"
            );

    private final ITextureData hoveredTexture =
            new TextureDataSubsection(
                    TEXTURE,
                    89, 24,
                    0, 0,
                    89, 12
            );

    private final ITextureData normalTexture =
            new TextureDataSubsection(
                    TEXTURE,
                    89, 24,
                    0, 12,
                    89, 12
            );

    private ResourceLocation selectedPath;

    public BreakthroughButton(UIFrame frame) {
        super(frame, 0, 0);

        setWidth(normalTexture.getWidth());
        setHeight(normalTexture.getHeight());
        setVisible(false);

        EasyLabel label = new EasyLabel(frame);
        label.setText(Component.translatable("ascension.gui.attempt_breakthrough"));
        label.setTextColor(-1);
        label.setWidth(85);
        label.setHeight(8);
        label.setScaleToFit(true);
        label.setTextScale(0.75F);

        label.getPositioning().setX(2);
        label.getPositioning().setY(2);

        label.setTextPositioningX(EasyLabel.TextPositionRule.CENTER);
        label.setTextPositioningY(EasyLabel.TextPositionRule.CENTER);

        addChild(label);
    }

    public void setPath(ResourceLocation path) {
        this.selectedPath = path;
    }

    @Override
    public void onClick() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || selectedPath == null) {
            return;
        }

        IEntityData entityData = minecraft.player.getData(ModAttachments.ENTITY_DATA);
        IPathData pathData = entityData.getPathData(selectedPath);


        if (pathData == null || !pathData.canStartTribulation(entityData)) {
            return;
        }

        PacketDistributor.sendToServer(new StartTribulationPayload(selectedPath));
        setVisible(false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (isHovered() || isPressed()) {
            hoveredTexture.render(guiGraphics);
        } else {
            normalTexture.render(guiGraphics);
        }
    }
}