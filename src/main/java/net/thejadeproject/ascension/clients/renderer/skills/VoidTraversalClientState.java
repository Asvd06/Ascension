package net.thejadeproject.ascension.clients.renderer.skills;

public final class VoidTraversalClientState {

    private static boolean active = false;
    private static int ticksRemaining = 0;
    private static int maxTicks = 1;

    private VoidTraversalClientState() {
    }

    public static void enter(int durationTicks) {
        active = true;
        ticksRemaining = Math.max(1, durationTicks);
        maxTicks = Math.max(1, durationTicks);
    }

    public static void exit() {
        active = false;
        ticksRemaining = 0;
        maxTicks = 1;
    }

    public static void clientTick() {
        if (!active) return;

        ticksRemaining--;

        if (ticksRemaining <= 0) {
            exit();
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static float intensity() {
        if (!active) return 0.0F;

        int age = maxTicks - ticksRemaining;
        float fadeIn = Math.min(1.0F, age / 8.0F);
        float fadeOut = Math.min(1.0F, ticksRemaining / 8.0F);

        return Math.min(fadeIn, fadeOut);
    }

    public static int ticksRemaining() {
        return ticksRemaining;
    }
}