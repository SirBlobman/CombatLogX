package com.griefdefender.api;

import java.util.UUID;

import com.griefdefender.api.claim.ClaimManager;
import org.jetbrains.annotations.Nullable;

public interface Core {
    @Nullable
    User getUser(UUID uuid);

    ClaimManager getClaimManager(UUID worldId);
}