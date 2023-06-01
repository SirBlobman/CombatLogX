package combatlogx.expansion.compatibility.husksync;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import net.william278.husksync.data.BukkitInventoryMap;
import net.william278.husksync.data.UserData;
import net.william278.husksync.player.User;

public final class PlayerData {
    private final Player player;
    private final User user;
    private final UserData userData;
    private final BukkitInventoryMap inventory;

    private boolean keepInventory;
    private boolean keepLevel;
    private int totalExperience;
    private int newLevel;
    private float newExperience;

    public PlayerData(@NotNull Player player, @NotNull User user, @NotNull UserData userData,
                      @NotNull BukkitInventoryMap inventory) {
        this.player = player;
        this.user = user;
        this.userData = userData;
        this.inventory = inventory;
        this.keepInventory = false;
        this.keepLevel = false;
        this.totalExperience = 0;
        this.newLevel = 0;
        this.newExperience = 0.0F;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    public @NotNull User getUser() {
        return this.user;
    }

    public @NotNull UserData getUserData() {
        return this.userData;
    }

    public @NotNull BukkitInventoryMap getInventory() {
        return this.inventory;
    }

    public boolean isKeepInventory() {
        return this.keepInventory;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    public boolean isKeepLevel() {
        return this.keepLevel;
    }

    public void setKeepLevel(boolean keepLevel) {
        this.keepLevel = keepLevel;
    }

    public int getTotalExperience() {
        return this.totalExperience;
    }

    public void setTotalExperience(int totalExperience) {
        this.totalExperience = totalExperience;
    }

    public int getNewLevel() {
        return this.newLevel;
    }

    public void setNewLevel(int newLevel) {
        this.newLevel = newLevel;
    }

    public float getNewExperience() {
        return this.newExperience;
    }

    public void setNewExperience(float newExperience) {
        this.newExperience = newExperience;
    }
}
