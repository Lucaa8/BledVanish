package ch.luca008.BledVanish;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ch.luca008.BledVanish.BledVanish.VanishMode;

import javax.annotation.Nonnull;

public class VanishCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("§cYou must be a player to use this command!");
            return false;
        }

        BledVanish v = BledVanish.getInstance();
        Player player = (Player) sender;

        if(args.length == 0) {

            boolean currentState = v.isVanished(player);
            BledVanish.getInstance().setVanished(player, !currentState, false);
            if(currentState) {
                player.sendMessage("§aTu es maintenant visible!");
            } else {
                player.sendMessage("§aTu es maintenant invisible!");
            }

        } else {

            String arg1 = args[0].toUpperCase();
            try {
                VanishMode mode = VanishMode.valueOf(arg1);
                mode.set(player);
                player.sendMessage("§aTu es maintenant en mode: §b" + mode.name().toLowerCase());
            } catch (Exception e) {
                VanishMode currentMode = VanishMode.get(player);
                boolean currentState = v.isVanished(player);
                player.sendMessage("§6------ §eVanish §6------");
                player.sendMessage("§aTu es actuellement " + (currentState ? "§ccaché" : "§bvisible"));
                player.sendMessage("§aTu es actuellement en mode: §b" + currentMode.name().toLowerCase());
                for(VanishMode mode : VanishMode.values()) {
                    player.sendMessage("§e- §6/v " + mode.name().toLowerCase() + "§e: " + mode.getDescription());
                }
                player.sendMessage("§6-------------------");
            }

        }

        return true;

    }
}
