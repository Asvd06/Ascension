package net.thejadeproject.ascension.refactor_packages.gui.elements.introspection.path_display;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.elements.EasyTooltip;
import net.lucent.easygui.gui.events.EasyEvents;
import net.lucent.easygui.gui.events.type.EasyEvent;
import net.lucent.easygui.gui.events.type.EasyMouseEvent;
import net.lucent.easygui.gui.textures.ITextureData;
import net.lucent.easygui.gui.textures.TextureDataSubsection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.paths.data.IPathData;
import net.thejadeproject.ascension.refactor_packages.paths.data.foundation.FoundationPathData;
import net.thejadeproject.ascension.refactor_packages.paths.data.foundation.RealmFoundation;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class FoundationProgressBar extends RenderableElement {
    ResourceLocation selectedPath;
    ResourceLocation textureIdentifier = ResourceLocation.fromNamespaceAndPath(
            AscensionCraft.MOD_ID,
            "textures/gui/main/path_menu/path_menu.png"
    );
    private ArrayList<ITextureData> tiers = new ArrayList<>(){{
        add(new TextureDataSubsection(
                textureIdentifier,
                234,287,
                175,184,
                9,84
        ));
        add(new TextureDataSubsection(
                textureIdentifier,
                234,287,
                186,184,
                9,84
        ));
        add(new TextureDataSubsection(
                textureIdentifier,
                234,287,
                197,184,
                9,84
        ));
        add(new TextureDataSubsection(
                textureIdentifier,
                234,287,
                208,184,
                9,84
        ));
        add(new TextureDataSubsection(
                textureIdentifier,
                234,287,
                208,184,
                9,84
        ));
        add(new TextureDataSubsection(
                textureIdentifier,
                234,287,
                219,184,
                9,84
        ));
    }};

    DecimalFormat format = new DecimalFormat("0.##");
    EasyTooltip tooltip;
    boolean hovered;
    public FoundationProgressBar(UIFrame frame, ResourceLocation path) {
        super(frame);
        this.selectedPath = path;
        setWidth(tiers.getFirst().getWidth());
        setHeight(tiers.getFirst().getHeight());
        tooltip = new EasyTooltip(frame);
        tooltip.setActive(true);
        addEventListener(EasyEvents.MOUSE_MOVE_EVENT,this::onMouseMove);
        addEventListener(EasyEvents.GLOBAL_MOUSE_MOVE_EVENT,this::onMouseMoveGlobal);
    }
    public void onMouseMoveGlobal(EasyEvent event){
        if(!(event instanceof EasyMouseEvent easyMouseEvent)) return;

        if(hovered && !isPointBounded(easyMouseEvent.getMouseX(),easyMouseEvent.getMouseY())) {
            hovered=false;
            if(getUiFrame().getTooltip() == tooltip) getUiFrame().setTooltip(null);
        }
    }
    public void onMouseMove(EasyEvent event){
        if(!(event instanceof EasyMouseEvent easyMouseEvent)) return;
        hovered = true;
    }

    public void setPath(ResourceLocation path){
        this.selectedPath = path;
    }

    public double getProgress(){
        IEntityData entityData = Minecraft.getInstance().player.getData(ModAttachments.ENTITY_DATA);
        IPathData pathData = entityData.getPathData(selectedPath);
        if(!(pathData instanceof FoundationPathData foundationPathData)) return 0.0;

        return foundationPathData.getCurrentFoundation().getProgressInStage();
    }
    public ITextureData getBarTexture(){
        IEntityData entityData = Minecraft.getInstance().player.getData(ModAttachments.ENTITY_DATA);
        IPathData pathData = entityData.getPathData(selectedPath);
        if(!(pathData instanceof FoundationPathData foundationPathData)) return tiers.getFirst();
        return tiers.get(foundationPathData.getCurrentFoundation().getFoundationRealm());
    }
    @Override
    protected void run(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.run(guiGraphics, mouseX, mouseY, partialTick);
        if(hovered){
            IEntityData entityData = Minecraft.getInstance().player.getData(ModAttachments.ENTITY_DATA);
            IPathData pathData = entityData.getPathData(selectedPath);
            if(!(pathData instanceof FoundationPathData foundationPathData)) return;
            //System.out.println("show progress for path : "+selectedPath);
            tooltip = new EasyTooltip(getUiFrame());
            RealmFoundation foundation = foundationPathData.getCurrentFoundation();
            double progressPercentage = Math.clamp(foundation.getProgressInStage() * 100.0D, 0.0D, 100.0D);
            Component tooltipText = foundation.getCurrentRealmName().copy().append(Component.literal(" (" + format.format(progressPercentage) + "%)"));
            tooltip.appendText(tooltipText);
            getUiFrame().setTooltip(tooltip);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ITextureData textureData = getBarTexture();
        double progress = getProgress();
        textureData.render(guiGraphics,textureData.getWidth(), (int) (textureData.getHeight()*(progress)));
    }
}