package ch.luca008.BledVanish;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class VanishStateChangeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;
    private final Player player;
    private final boolean newState;

    public VanishStateChangeEvent(Player player, boolean newState) {
        this.player = player;
        this.newState = newState;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Nonnull
    public Player getPlayer() {
        return this.player;
    }

    public boolean isCurrentlyVanished() {
        return !this.newState;
    }

    public boolean willBeVanished() {
        return this.newState;
    }

}
