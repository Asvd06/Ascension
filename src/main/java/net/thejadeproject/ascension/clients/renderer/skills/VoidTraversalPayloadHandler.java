package net.thejadeproject.ascension.clients.renderer.skills;

import net.thejadeproject.ascension.refactor_packages.network.client_bound.skills.SyncVoidTraversalState;

public final class VoidTraversalPayloadHandler {

    private VoidTraversalPayloadHandler() {
    }

    public static void handle(SyncVoidTraversalState payload) {
        if (payload.active()) {
            VoidTraversalClientState.enter(payload.durationTicks());
        } else {
            VoidTraversalClientState.exit();
        }
    }
}