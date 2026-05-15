package net.thejadeproject.ascension.refactor_packages.skill_casting.casting;

import net.minecraft.network.chat.Component;

public class CastResult {
    public static CastResult success(){
        return new CastResult(Type.SUCCESS);
    }
    public static CastResult fail(){
        return new CastResult(Type.FAILURE);
    }

    public enum Type {
        SUCCESS,
        FAILURE
    }

    public final Type type;
    public final Component message;

    public CastResult(Type type) {
        this(type, null);
    }

    public CastResult(Type type, Component message) {
        this.type = type;
        this.message = message;
    }

    public boolean isSuccess() {
        return this.type == Type.SUCCESS;
    }
}
