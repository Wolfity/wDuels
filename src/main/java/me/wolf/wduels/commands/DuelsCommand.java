package me.wolf.wduels.commands;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.commands.impl.*;
import me.wolf.wduels.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DuelsCommand extends Command {
    private final DuelsPlugin plugin;
    private final List<SubCommand> subCommands = new ArrayList<>();
    private final List<SubCommand> adminCommands = new ArrayList<>();

    public DuelsCommand(final DuelsPlugin plugin) {
        super("duels");
        this.plugin = plugin;
        addSubCommand(false, new JoinDuelsCommand(), new LeaveDuelsCommand(), new AvailableArenasCommand(), new DuelCommand());
        addSubCommand(true, new AddSpawnCommand(), new CreateArenaCommand(), new DeleteArenaCommand(), new SetSpawnCommand());
    }

    private void addSubCommand(final boolean admin, final SubCommand... subs) {
        if (!admin) {
            subCommands.addAll(Arrays.asList(subs));
        } else adminCommands.addAll(Arrays.asList(subs));
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender instanceof Player) {
            final Player player = (Player) commandSender;

            if (args.length > 0) {
                for (SubCommand subCommand : Stream.concat(subCommands.stream(), adminCommands.stream()).collect(Collectors.toList())) {
                    if (args[0].equalsIgnoreCase(subCommand.getCommandName())) {
                        subCommand.executeCommand(player, args, plugin);
                    }
                }

            } else {
                final StringBuilder msg = new StringBuilder();
                if (player.hasPermission("duels.admin")) {
                    msg.append("&7[----------&aDuels Help&7----------] \n");
                    adminCommands.forEach(subCommand -> msg.append(subCommand.getUsage()).append(" - ").append(subCommand.getDescription()).append("\n"));
                }
                subCommands.forEach(subCommand -> msg.append(subCommand.getUsage()).append(" - ").append(subCommand.getDescription()).append("\n"));
                msg.append("&7[-------------------------------]\n");

                player.sendMessage(Utils.colorize(msg.toString()));
            }
        }
        return false;
    }
}
