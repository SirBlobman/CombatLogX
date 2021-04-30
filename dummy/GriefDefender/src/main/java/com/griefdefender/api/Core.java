package com.griefdefender.api;

import java.util.UUID;

import com.griefdefender.api.claim.ClaimManager;

public interface Core {
    User getUser(UUID uuid);

    ClaimManager getClaimManager(UUID worldId);
}