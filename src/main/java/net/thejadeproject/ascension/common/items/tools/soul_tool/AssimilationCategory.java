package net.thejadeproject.ascension.common.items.tools.soul_tool;

public enum AssimilationCategory {
    CORE("core"),
    HARVEST("harvest"),
    FLOW("flow"),
    SPIRIT("spirit");

    private final String id;

    AssimilationCategory(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }
}