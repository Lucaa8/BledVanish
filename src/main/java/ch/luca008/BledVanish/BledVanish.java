package ch.luca008.BledVanish;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class BledVanish extends JavaPlugin {

    private static BledVanish instance;
    private static NamespacedKey vanishedKey;
    private static NamespacedKey vanishModeKey;
    private static Permission vanishPermission;
    private static PotionEffect vanishPotion;
    private static PotionEffect glowPotion;

    public static BledVanish getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        vanishedKey = new NamespacedKey(this, "vanished");
        vanishModeKey = new NamespacedKey(this, "vanishMode");
        vanishPermission = getServer().getPluginManager().getPermission("bledvanish.command.vanish");
        vanishPotion = new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false, true);
        glowPotion = new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 0, false, false, false);
        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("vanish").setTabCompleter(new VanishCommandCompleter());
        getServer().getPluginManager().registerEvents(new Listener(), this);
    }

    public void onDisable() {

    }

    protected void updateVanishState(Player player)
    {
        boolean currentState = isVanished(player);
        if(currentState)
        {
            player.addPotionEffect(vanishPotion);
            player.addPotionEffect(glowPotion);
        }
        else
        {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.removePotionEffect(PotionEffectType.GLOWING);
        }
        for(Player p : Bukkit.getOnlinePlayers())
        {
            //"canVanish" here is a little bit misleading and would be "!canSeeVanished" but in this plugin, there is only one permission
            //to be able to vanish AND see vanished players.
            if(currentState && !canVanish(p))
            {
                p.hidePlayer(this, player);
            }
            else
            {
                p.showPlayer(this, player);
            }
        }
    }

    /**
     * Change a player's visibility state. Call the cancellable event {@link VanishStateChangeEvent}.
     * <p>
     * THIS METHOD DOES NOT CHECK IF PLAYER HAS PERMISSION please use {@link #canVanish(Player)} before using it!
     * <p>
     * If the player already have the given state nothing happen, this call is silently ignored
     * @param player The Nonnull player which need to have their visibility changed
     * @param state The new visibility state
     * @param force If force is set to true, everything stays the same but the {@link VanishStateChangeEvent} is not called (to avoid it being cancelled).
     */
    public void setVanished(@Nonnull Player player, boolean state, boolean force)
    {
        boolean currentState = isVanished(player);
        if(currentState == state)
        {
            return;
        }
        if(force)
        {
            player.getPersistentDataContainer().set(vanishedKey, PersistentDataType.BOOLEAN, state);
            updateVanishState(player);
        }
        else
        {
            VanishStateChangeEvent ev = new VanishStateChangeEvent(player, state);
            Bukkit.getPluginManager().callEvent(ev);
            if(!ev.isCancelled())
            {
                player.getPersistentDataContainer().set(vanishedKey, PersistentDataType.BOOLEAN, state);
                updateVanishState(player);
            }
        }
    }

    /**
     * Check if a player is currently vanished or not. This method also check if the player has the permission to be in a vanished state with {@link #canVanish(Player)}
     * @param player The player you want to check
     * @return true if the player has the permission to be in a vanished state AND is currently in a vanished state, false otherwise.
     */
    public boolean isVanished(@Nonnull Player player)
    {
        PersistentDataContainer container = player.getPersistentDataContainer();
        //The canVanish check is mandatory to avoid cases or a moderator vanish themselves before being demoted and staying vanished afterward
        return canVanish(player) && container.has(vanishedKey) && Boolean.TRUE.equals(container.get(vanishedKey, PersistentDataType.BOOLEAN));
    }

    /**
     * Check if a player CAN be in a vanished state (has the permission). THIS METHOD DOES NOT RETURN THE CURRENT VANISHED STATE, please check {@link #isVanished(Player)} instead.
     * @param player The player you want to check
     * @return true if the player can vanish, false otherwise.
     */
    public boolean canVanish(@Nonnull Player player)
    {
        return player.hasPermission(vanishPermission);
    }

    public enum VanishMode {
        //When a player with vanish permission joins;
        ALWAYS("Tu rejoins le serveur de façon invisible et silencieuse, mode vanish actif par défaut."), //will be forced in vanish mode
        NEVER("Tu rejoins le serveur comme un joueur standard, mode vanish inactif par défaut."), //will be forced in visible mode
        SAVE("Tu rejoins le serveur comme tu l'as quitté."); //will keep the vanish state he had before leaving the server last time

        private final String description;

        VanishMode(String modeDescription){
            this.description = modeDescription;
        }

        public String getDescription(){
            return description;
        }

        public void set(@Nonnull Player player)
        {
            player.getPersistentDataContainer().set(vanishModeKey, PersistentDataType.STRING, name());
        }

        public static VanishMode get(@Nonnull Player player)
        {
            PersistentDataContainer storage = player.getPersistentDataContainer();
            return storage.has(vanishModeKey) ? VanishMode.valueOf(storage.get(vanishModeKey, PersistentDataType.STRING)) : SAVE;
        }

    }

}
