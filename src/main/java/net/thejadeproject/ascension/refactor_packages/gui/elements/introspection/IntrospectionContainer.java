package net.thejadeproject.ascension.refactor_packages.gui.elements.introspection;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.layout.positioning.rules.PositioningRules;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.gui.elements.info_elements.IInformationContainer;
import net.thejadeproject.ascension.refactor_packages.gui.elements.introspection.main.MainContainer;
import net.thejadeproject.ascension.refactor_packages.gui.elements.introspection.path_display.PathDisplayContainer;
import net.thejadeproject.ascension.refactor_packages.gui.elements.introspection.skill_display.SkillDisplayContainer;

public class IntrospectionContainer extends RenderableElement {

    public MainContainer mainContainer;
    public SkillDisplayContainer skillDisplayContainer;
    public PathDisplayContainer pathDisplayContainer;

    public NavButton mainBtn;
    public NavButton skillBtn;
    public NavButton pathBtn;

    public IntrospectionContainer(UIFrame frame) {
        super(frame);
        System.out.println("creating c1");
        getPositioning().setPositioningRule(PositioningRules.CENTER);
        mainContainer = new MainContainer(frame);
        mainContainer.setActive(true);
        addChild(mainContainer);


        System.out.println("creating c2");
        skillDisplayContainer = new SkillDisplayContainer(frame);
        skillDisplayContainer.setActive(false);
        addChild(skillDisplayContainer);

        System.out.println("creating c3");
        pathDisplayContainer = new PathDisplayContainer(frame);
        pathDisplayContainer.setActive(false);
        addChild(pathDisplayContainer);


        System.out.println("creating b1");
        mainBtn = new NavButton(frame,"main",ResourceLocation.fromNamespaceAndPath(
                AscensionCraft.MOD_ID,
                "textures/gui/main/top_tab_buttons.png"
        ));
        mainBtn.getPositioning().setX(-mainContainer.getWidth()/2+4);
        mainBtn.getPositioning().setY(-mainContainer.getHeight()/2-27);
        addChild(mainBtn);
        System.out.println("creating b2");
        skillBtn = new NavButton(frame,"skill",ResourceLocation.fromNamespaceAndPath(
                AscensionCraft.MOD_ID,
                "textures/gui/main/skill_menu/skill_tab_button.png"
        ));
        skillBtn.getPositioning().setX(-mainContainer.getWidth()/2+30);
        skillBtn.getPositioning().setY(-mainContainer.getHeight()/2-27);
        addChild(skillBtn);
        System.out.println("creating b3");
        pathBtn = new NavButton(frame,"path",ResourceLocation.fromNamespaceAndPath(
                AscensionCraft.MOD_ID,
                "textures/gui/main/path_menu/path_buttons.png"
        ));
        pathBtn.getPositioning().setX(-mainContainer.getWidth()/2+56);
        pathBtn.getPositioning().setY(-mainContainer.getHeight()/2-27);
        addChild(pathBtn);




    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

    }

    public void openScreen(String screen){
        switch (screen) {
            case "main" -> {
                mainContainer.setActive(true);
                skillDisplayContainer.setActive(false);
                pathDisplayContainer.setActive(false);
                pathBtn.setActiveBtn(false);
                skillBtn.setActiveBtn(false);
                mainBtn.setActiveBtn(true);
            }
            case "skill" -> {
                mainContainer.setActive(false);
                skillDisplayContainer.setActive(true);
                pathDisplayContainer.setActive(false);
                pathBtn.setActiveBtn(false);
                skillBtn.setActiveBtn(true);
                mainBtn.setActiveBtn(false);
            }
            case "path" -> {
                mainContainer.setActive(false);
                skillDisplayContainer.setActive(false);
                pathDisplayContainer.setActive(true);
                pathBtn.setActiveBtn(true);
                skillBtn.setActiveBtn(false);
                mainBtn.setActiveBtn(false);
            }
        }
    }
}
