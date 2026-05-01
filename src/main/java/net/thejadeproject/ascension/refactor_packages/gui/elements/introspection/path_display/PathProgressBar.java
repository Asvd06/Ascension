package net.thejadeproject.ascension.refactor_packages.gui.elements.introspection.path_display;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.minecraft.resources.ResourceLocation;

public class PathProgressBar extends RenderableElement {
    ResourceLocation selectedPath;
    public PathProgressBar(UIFrame frame, ResourceLocation path) {
        super(frame);
        this.selectedPath = path;
    }

    public void setPath(ResourceLocation path){
        this.selectedPath = path;
    }
}
