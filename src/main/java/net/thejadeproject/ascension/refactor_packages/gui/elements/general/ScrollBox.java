package net.thejadeproject.ascension.refactor_packages.gui.elements.general;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.events.EasyEvents;
import net.lucent.easygui.gui.events.type.EasyEvent;
import net.lucent.easygui.gui.events.type.EasyMouseEvent;

public class ScrollBox extends RenderableElement {
    public boolean useCustomChildAdditionLogic = true;

    private int yOffset = 0;
    private final int scrollRate;

    public ScrollBox(UIFrame frame, int scrollRate) {
        super(frame);
        this.scrollRate = scrollRate;
        setShouldCull(true);
        addEventListener(EasyEvents.MOUSE_SCROLL_EVENT, this::onMouseScroll);
    }

    public void onMouseScroll(EasyEvent event) {
        if (event.isCanceled()) return;
        if (!(event instanceof EasyMouseEvent mouseEvent)) return;
        if (mouseEvent.getDeltaY() == 0) return;

        scroll(mouseEvent.getDeltaY() < 0 ? -1 : 1);
        event.setCanceled(true);
    }

    public int getMaxYScroll() {
        int contentBottom = 0;

        for (RenderableElement element : getChildren()) {
            int bottom = element.getPositioning().getY() + element.getHeight() + yOffset;
            if (bottom > contentBottom) {
                contentBottom = bottom;
            }
        }

        return Math.max(0, contentBottom - getHeight());
    }

    public void updatePos(RenderableElement element) {
        if (!getChildren().isEmpty()) {
            RenderableElement lastChild = getChildren().getLast();

            if (lastChild.getPositioning().getX() + lastChild.getWidth() + element.getWidth() < getWidth()) {
                element.getPositioning().setFromRawY(lastChild.getPositioning().getRawY() + element.getHeight());
            } else {
                element.getPositioning().setFromRawY(lastChild.getPositioning().getRawY());
                element.getPositioning().setFromRawX(lastChild.getPositioning().getRawX() + lastChild.getWidth());
            }
        }
    }

    @Override
    public void addChild(RenderableElement element) {
        if (useCustomChildAdditionLogic) {
            updatePos(element);
        }

        super.addChild(element);

        if (getHeight() > 0) {
            updateVisibility(element);
        }
    }

    public void refreshVisibility() {
        for (RenderableElement element : getChildren()) {
            updateVisibility(element);
        }
    }

    public void updateVisibility(RenderableElement element) {
        boolean visible =
                element.getPositioning().getY() + element.getHeight() > 0
                        && element.getPositioning().getY() < getHeight();

        element.setVisible(visible);
    }

    public void updateChildrenY(int change) {
        for (RenderableElement element : getChildren()) {
            element.getPositioning().setY(element.getPositioning().getY() + change);
            updateVisibility(element);
        }
    }

    public void scroll(int amount) {
        if (amount == 0) return;

        int change = Math.abs(amount) * scrollRate;
        int oldYOffset = yOffset;

        if (amount < 0) {
            yOffset = Math.min(yOffset + change, getMaxYScroll());
        } else {
            yOffset = Math.max(0, yOffset - change);
        }

        if (oldYOffset != yOffset) {
            updateChildrenY(oldYOffset - yOffset);
        } else {
            refreshVisibility();
        }
    }
}