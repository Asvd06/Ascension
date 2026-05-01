package net.thejadeproject.ascension.refactor_packages.gui.elements.introspection.path_display;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.thejadeproject.ascension.refactor_packages.gui.elements.general.ScrollBox;

public class PathOptionsScrollBox extends ScrollBox {
    public PathOptionsScrollBox(UIFrame frame) {
        super(frame, 5);
        setWidth(89);
        setHeight(91);
    }

    @Override
    public void updatePos(RenderableElement element) {
        if(getChildren().isEmpty()) return;
        element.getPositioning().setFromRawY(getChildren().getLast().getPositioning().getRawY()+getChildren().getLast().getHeight()+2);
    }


}
