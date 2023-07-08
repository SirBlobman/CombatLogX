package combatlogx.expansion.compatibility.husksync;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.william278.husksync.data.BukkitInventoryMap;
import net.william278.husksync.data.UserData;
import net.william278.husksync.player.User;

public final class PlayerData {
    private final Player player;
    private final Location location;

    private User user;
    private UserData userData;
    private BukkitInventoryMap inventory;
    private boolean keepInventory;
    private boolean keepLevel;
    private int totalExperience;
    private int newLevel;
    private float newExperience;

    public PlayerData(@NotNull Player player, @NotNull Location location) {
        this.player = player;
        this.location = location;

        this.user = null;
        this.userData = null;
        this.inventory = null;

        this.keepInventory = false;
        this.keepLevel = false;
        this.totalExperience = 0;
        this.newLevel = 0;
        this.newExperience = 0.0F;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    public @NotNull Location getLocation() {
        return this.location;
    }

    public @NotNull Optional<User> getUser() {
        return Optional.ofNullable(this.user);
    }

    public void setUser(@NotNull User user) {
        this.user = user;
    }

    public @NotNull Optional<UserData> getUserData() {
        return Optional.ofNullable(this.userData);
    }

    public void setUserData(@NotNull UserData userData) {
        this.userData = userData;
    }

    public @NotNull Optional<BukkitInventoryMap> getInventory() {
        return Optional.ofNullable(this.inventory);
    }

    public void setInventory(@NotNull BukkitInventoryMap inventory) {
        this.inventory = inventory;
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
