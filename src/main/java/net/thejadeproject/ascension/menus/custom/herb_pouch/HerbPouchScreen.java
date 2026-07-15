package net.thejadeproject.ascension.menus.custom.herb_pouch;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.network.PacketDistributor;
import net.lucent.easygui.screen.EasyContainerScreen;
import net.thejadeproject.ascension.common.items.data_components.ModDataComponents;
import net.thejadeproject.ascension.common.items.data_components.herb_pouch.HerbPouchExtractionMode;
import net.thejadeproject.ascension.common.items.herbs.HerbQuality;
import net.thejadeproject.ascension.refactor_packages.gui.elements.herb_pouch.HerbPouchElement;
import net.thejadeproject.ascension.refactor_packages.network.server_bound.herb_pouch.ExtractHerbFromPouchPayload;
import net.thejadeproject.ascension.refactor_packages.network.server_bound.herb_pouch.InsertCarriedHerbIntoPouchPayload;
import net.thejadeproject.ascension.util.ModTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HerbPouchScreen
        extends EasyContainerScreen<HerbPouchMenu> {

    private static final int HERB_START_X = 7;
    private static final int HERB_START_Y = 17;

    private static final int HERB_COLUMNS = 9;

    private static final int HERB_VISIBLE_ROWS = HerbPouchElement.HERB_VISIBLE_ROWS;

    private static final int HERBS_PER_PAGE = HERB_COLUMNS * HERB_VISIBLE_ROWS;

    private static final int PREVIOUS_PAGE_X = 121;
    private static final int NEXT_PAGE_X = 162;

    private static final int PAGE_BUTTON_Y = 4;
    private static final int PAGE_BUTTON_WIDTH = 10;
    private static final int PAGE_BUTTON_HEIGHT = 12;

    private int page;

    public HerbPouchScreen(HerbPouchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        getUIFrame().setRoot(new HerbPouchElement(getUIFrame(), getMenu()));
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Vec2 root = getUIFrame().getRoot().getGlobalPoint();

        int relativeX = (int) root.x - leftPos;
        int relativeY = (int) root.y - topPos;

        guiGraphics.drawString(
                font,
                Component.translatable(
                        "item.ascension.herb_pouch"
                ),
                relativeX + 8,
                relativeY + 6,
                0xE8D8A8,
                false
        );

        guiGraphics.drawString(
                font,
                Component.literal("<"),
                relativeX + PREVIOUS_PAGE_X + 2,
                relativeY + 6,
                0xE8D8A8,
                false
        );

        Component pageText =
                Component.translatable(
                        "gui.ascension.herb_pouch.page",
                        page + 1,
                        getPageCount());

        guiGraphics.drawCenteredString(
                font,
                pageText,
                relativeX + 146,
                relativeY + 6,
                0xE8D8A8
        );

        guiGraphics.drawString(
                font,
                Component.literal(">"),
                relativeX + NEXT_PAGE_X + 2,
                relativeY + 6,
                0xE8D8A8,
                false
        );

        int inventoryTitleY = relativeY + HerbPouchElement.PLAYER_INVENTORY_BG_Y + 1;

        guiGraphics.drawString(
                font,
                playerInventoryTitle,
                relativeX + 8,
                inventoryTitleY,
                0xE8D8A8,
                false
        );

        Component capacityText =
                Component.translatable(
                        "gui.ascension.herb_pouch.capacity",
                        menu.getStoredCount(),
                        menu.getCapacity());

        guiGraphics.drawString(
                font,
                capacityText,
                relativeX
                        + 168
                        - font.width(capacityText),
                inventoryTitleY,
                0xE8D8A8,
                false
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        clampPage();

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        renderHerbSummaries(guiGraphics, mouseX, mouseY);
    }

    private void renderHerbSummaries(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Vec2 root = getUIFrame().getRoot().getGlobalPoint();
        int baseX = (int) root.x + HERB_START_X;
        int baseY = (int) root.y + HERB_START_Y;
        List<ItemStack> summaries = menu.getSummaryStacks();

        int firstSummaryIndex = page * HERBS_PER_PAGE;

        int visibleCount = Math.max(0, Math.min(HERBS_PER_PAGE, summaries.size() - firstSummaryIndex));

        for (int displayIndex = 0; displayIndex < visibleCount; displayIndex++) {
            int summaryIndex = firstSummaryIndex + displayIndex;
            ItemStack stack = summaries.get(summaryIndex);

            int x = baseX + displayIndex % HERB_COLUMNS * 18;
            int y = baseY + displayIndex / HERB_COLUMNS * 18;

            guiGraphics.renderItem(stack, x, y);

            renderAgeTierMarker(guiGraphics, stack, x, y);
        }

        for (int displayIndex = 0; displayIndex < visibleCount; displayIndex++) {
            int summaryIndex = firstSummaryIndex + displayIndex;
            ItemStack stack = summaries.get(summaryIndex);

            int x = baseX + displayIndex % HERB_COLUMNS * 18;
            int y = baseY + displayIndex / HERB_COLUMNS * 18;

            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                renderSummaryTooltip(
                        guiGraphics,
                        stack,
                        mouseX,
                        mouseY
                );

                break;
            }
        }
    }

    private void renderAgeTierMarker(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {
        Long ageComponent = stack.get(ModDataComponents.HERB_AGE_TIER.get());

        long age = ageComponent != null ? ageComponent : 0L;
        HerbQuality.AgeTier ageTier = HerbQuality.getAgeTier(age);
        Integer formattingColor = HerbQuality.getAgeColor(age).getColor();

        int color = formattingColor != null ? formattingColor : 0xFFFFFF;

        guiGraphics.drawString(
                font,
                ageTier.shortName(),
                x + 1,
                y + 1,
                color,
                true
        );
    }

    private void renderSummaryTooltip(GuiGraphics guiGraphics, ItemStack stack, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>(getTooltipFromContainerItem(stack));
        tooltip.add(Component.empty());

        tooltip.add(
                Component.translatable("gui.ascension.herb_pouch.stored_amount", stack.getCount())
                        .withStyle(ChatFormatting.GOLD)
        );

        tooltip.add(Component.empty());

        tooltip.add(
                Component.translatable("gui.ascension.herb_pouch.extract_first")
                        .withStyle(ChatFormatting.DARK_GRAY)
        );

        tooltip.add(
                Component.translatable("gui.ascension.herb_pouch.extract_last")
                        .withStyle(ChatFormatting.DARK_GRAY)
        );

        tooltip.add(
                Component.translatable("gui.ascension.herb_pouch.extract_age")
                        .withStyle(ChatFormatting.DARK_GRAY)
        );

        tooltip.add(
                Component.translatable("gui.ascension.herb_pouch.extract_all")
                        .withStyle(ChatFormatting.DARK_GRAY)
        );

        guiGraphics.renderTooltip(
                font,
                tooltip,
                Optional.empty(),
                mouseX,
                mouseY
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isInsidePageButton(mouseX, mouseY, PREVIOUS_PAGE_X)) {
            changePage(-1);
            return true;
        }

        if (isInsidePageButton(mouseX, mouseY, NEXT_PAGE_X)) {
            changePage(1);
            return true;
        }

        if (isInsideHerbArea(mouseX, mouseY) && !menu.getCarried().isEmpty()) {
            if (menu.getCarried().is(ModTags.Items.HERBS)) {
                PacketDistributor.sendToServer(
                        new InsertCarriedHerbIntoPouchPayload()
                );
            }

            return true;
        }

        if (button != 0 && button != 1) {
            return super.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            );
        }

        Vec2 root = getUIFrame().getRoot().getGlobalPoint();

        int baseX = (int) root.x + HERB_START_X;
        int baseY = (int) root.y + HERB_START_Y;
        List<ItemStack> summaries = menu.getSummaryStacks();
        int firstSummaryIndex = page * HERBS_PER_PAGE;
        int visibleCount = Math.max(0, Math.min(HERBS_PER_PAGE, summaries.size() - firstSummaryIndex));

        for (int displayIndex = 0; displayIndex < visibleCount; displayIndex++) {
            int x = baseX + displayIndex % HERB_COLUMNS * 18;

            int y = baseY + displayIndex / HERB_COLUMNS * 18;

            if (mouseX < x || mouseX >= x + 16 || mouseY < y || mouseY >= y + 16) {
                continue;
            }

            int summaryIndex = firstSummaryIndex + displayIndex;
            ItemStack selectedSummary = summaries.get(summaryIndex).copy();
            selectedSummary.setCount(1);

            HerbPouchExtractionMode mode;

            if (hasShiftDown()) {
                mode = button == 1 ? HerbPouchExtractionMode.ALL_FROM_HERB : HerbPouchExtractionMode.ALL_FROM_AGE_GROUP;
            } else {
                mode = button == 1 ? HerbPouchExtractionMode.LAST_ONE : HerbPouchExtractionMode.FIRST_ONE;
            }

            PacketDistributor.sendToServer(
                    new ExtractHerbFromPouchPayload(
                            selectedSummary,
                            mode
                    )
            );

            return true;
        }

        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isInsideHerbArea(mouseX, mouseY) && scrollY != 0) {
            changePage(scrollY > 0 ? -1 : 1);
            return true;
        }

        return super.mouseScrolled(
                mouseX,
                mouseY,
                scrollX,
                scrollY
        );
    }

    private void changePage(int amount) {
        page = Mth.clamp(page + amount, 0, getPageCount() - 1);
    }

    private void clampPage() {
        page = Mth.clamp(page, 0, getPageCount() - 1);
    }

    private int getPageCount() {
        int summaryCount = menu.getSummaryStacks().size();
        return Math.max(1, (summaryCount + HERBS_PER_PAGE - 1) / HERBS_PER_PAGE);
    }

    private boolean isInsideHerbArea(double mouseX, double mouseY) {
        Vec2 root = getUIFrame().getRoot().getGlobalPoint();

        int x = (int) root.x + HERB_START_X;
        int y = (int) root.y + HERB_START_Y;
        int width = HERB_COLUMNS * 18;
        int height = HERB_VISIBLE_ROWS * 18;

        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private boolean isInsidePageButton(double mouseX, double mouseY, int localX) {
        Vec2 root = getUIFrame().getRoot().getGlobalPoint();

        int x = (int) root.x + localX;
        int y = (int) root.y + PAGE_BUTTON_Y;
        return mouseX >= x && mouseX < x + PAGE_BUTTON_WIDTH && mouseY >= y && mouseY < y + PAGE_BUTTON_HEIGHT;
    }

    public Rect2i getUsedArea() {
        Vec2 point = getUIFrame().getRoot().getGlobalPoint();

        return new Rect2i(
                (int) point.x,
                (int) point.y,
                getUIFrame().getRoot().getWidth(),
                getUIFrame().getRoot().getHeight()
        );
    }
}