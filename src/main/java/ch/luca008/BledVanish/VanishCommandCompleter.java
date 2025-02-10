package ch.luca008.BledVanish;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VanishCommandCompleter implements TabCompleter {

    private final List<String> values;

    public VanishCommandCompleter() {
        values = Stream.concat(
                Stream.of("help"),
                Arrays.stream(BledVanish.VanishMode.values()).map(Enum::name).map(String::toLowerCase)
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if(args.length == 1)
        {
            return values;
        }
        return List.of();
    }
}
