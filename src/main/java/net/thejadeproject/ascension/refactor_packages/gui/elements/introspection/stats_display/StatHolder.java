package net.thejadeproject.ascension.refactor_packages.gui.elements.introspection.stats_display;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.layout.positioning.rules.PositioningRules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.stats.Stat;
import net.thejadeproject.ascension.refactor_packages.stats.StatSheet;

import java.util.Collection;

public class StatHolder extends RenderableElement {
    public StatHolder(UIFrame frame) {
        super(frame);
        setWidth(60);
        setHeight(113);
        getPositioning().setPositioningRule(PositioningRules.CENTER);
        getPositioning().setX(-getWidth()/2);
        getPositioning().setY(-getHeight()/2);
        addStats();
    }

    public void addStats(){
        StatSheet sheet =  Minecraft.getInstance().player.getData(ModAttachments.ENTITY_DATA).getActiveFormData().getStatSheet();
        Collection<Stat> stats = sheet.getStats();
        int y = 0;
        for(Stat stat : stats){
            StatsDisplay display = new StatsDisplay(getUiFrame(),stat);
            display.getPositioning().setY(y);
            display.getPositioning().setX(getWidth()/2);
            addChild(display);
            y+=20;
        }
    }


}
