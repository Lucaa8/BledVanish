package ch.luca008.BledVanish;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Listener implements org.bukkit.event.Listener {

    //This event hides a moderator if vanish enabled to every current online players
    //Priority LOWEST means this event is fired before every other PlayerJoinEvent, so the vanish state is accurate for every other plugins
    @EventHandler(priority = EventPriority.LOWEST)
    public void onModeratorJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BledVanish vanish = BledVanish.getInstance();
        if(vanish.canVanish(player)) {
            switch (BledVanish.VanishMode.get(player)){
                case NEVER -> vanish.setVanished(player, false, true);
                case ALWAYS -> vanish.setVanished(player, true, true);
                //Case SAVE do not change anything to the current player's vanish state
            }
        } else {
            //If a moderator which got demoted quit the server in a vanished state, the vanish get removed.
            vanish.setVanished(player, false, true);
        }
        vanish.updateVanishState(event.getPlayer());
    }

    //This event hides every moderator currently in vanish state when a normal player joins the server
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BledVanish vanish = BledVanish.getInstance();
        //The player who joined is moderator and can see every vanished player so no need to hide anyone
        if(vanish.canVanish(player)) {
            return;
        }
        for(Player p : Bukkit.getOnlinePlayers())
        {
            if(vanish.isVanished(p))
            {
                player.hidePlayer(vanish, p);
            }
        }
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if(event.getEntity() instanceof Player p && BledVanish.getInstance().isVanished(p)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickupArrow(PlayerPickupArrowEvent e) {
        if (BledVanish.getInstance().isVanished(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    // Silent chest will be enabled when a moderator is switching to vanish mode and disabled when they exit vanish mode
    // To avoid conflicts between the /silentcontainer command and the vanish mode call, the command will be disabled.
    @EventHandler
    public void onPlayerToggleSilentChest(PlayerCommandPreprocessEvent e)
    {
        String cmd = e.getMessage().toLowerCase(Locale.ENGLISH).split(" ")[0].substring(1);
        List<String> cmds = List.of("silentcontainer", "sc", "silent", "silentchest");

        if(cmds.contains(cmd) && e.getPlayer().hasPermission("OpenInv.silent"))
        {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cSilent Chest est actif uniquement lorsque tu es en vanish!");
        }
    }

}
