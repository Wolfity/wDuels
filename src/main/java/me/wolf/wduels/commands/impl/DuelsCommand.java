package me.wolf.wduels.commands.impl;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.arena.Arena;
import me.wolf.wduels.arena.ArenaManager;
import me.wolf.wduels.arena.ArenaState;
import me.wolf.wduels.commands.BaseCommand;
import me.wolf.wduels.constants.Messages;
import me.wolf.wduels.file.YamlConfig;
import me.wolf.wduels.game.GameType;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.player.PlayerState;
import me.wolf.wduels.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DuelsCommand extends BaseCommand {
    private final DuelsPlugin plugin;

    public DuelsCommand(DuelsPlugin plugin) {
        super("duels");
        this.plugin = plugin;
    }

    @Override
    protected void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        final ArenaManager arenaManager = plugin.getArenaManager();
        final YamlConfig arenaYml = plugin.getFileManager().getArenasConfigFile();
        final Player player = (Player) sender;

        if (args.length == 0) {
            tell(Messages.HELP_MSG);
        }

        if (args.length == 1) {
            switch (args[0]) {
                case "join":
                    joinDuels(player);
                    break;
                case "leave":
                    leaveDuels(player);
                    player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    break;
                case "available":
                    final StringBuilder result = new StringBuilder();
                    result.append(Utils.colorize("&aAvailable Arenas\n"));
                    for (final Arena available : arenaManager.getArenas()) {
                        if (available.getState() == ArenaState.PREQUEUE) {
                            result.append("&a- &2").append(available.getName()).append("\n");
                        }
                    }
                    tell(result.toString());
                    break;
                default:
                    tell(Messages.HELP_MSG);
                    break;

            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("duel")) {
                final Player invited = Bukkit.getPlayerExact(args[1]);
                if(canInvite(player, invited)) {
                    final DuelPlayer duelSender = plugin.getPlayerManager().getDuelPlayer(player.getUniqueId());
                    final DuelPlayer duelReceiver = plugin.getPlayerManager().getDuelPlayer(invited.getUniqueId());
                    sendRequest(duelSender, duelReceiver);
                }
            }
        }


        if (isAdmin() && args.length == 2) {
            final String name = args[1].toLowerCase();
            switch (args[0]) {
                case "createarena":
                    if (!plugin.getArenaManager().doesArenaExist(name)) {
                        arenaManager.createArena(name, arenaYml);
                        tell(Messages.ARENA_CREATED.replace("{arena}", name));
                    } else tell(Messages.ARENA_EXISTS);
                    break;
                case "deletearena":
                    if (arenaManager.doesArenaExist(name)) {
                        arenaManager.deleteArena(name, arenaYml);
                        tell(Messages.ARENA_DELETED.replace("{arena}", name));
                    } else tell(Messages.ARENA_NOT_FOUND);
                    break;
                case "addspawn":
                    addSpawn(arenaYml, name, player);
                    break;
                case "setspawn":
                    plugin.getConfig().set("spawn.world", player.getLocation().getWorld().getName());
                    plugin.getConfig().set("spawn.x", player.getLocation().getX());
                    plugin.getConfig().set("spawn.y", player.getLocation().getY());
                    plugin.getConfig().set("spawn.z", player.getLocation().getZ());
                    plugin.getConfig().set("spawn.pitch", player.getLocation().getPitch());
                    plugin.getConfig().set("spawn.yaw", player.getLocation().getYaw());
                    plugin.saveConfig();
                    player.sendMessage(Messages.SET_LOBBY_SPAWN);
                    break;
                default:
                    tell(Messages.ADMIN_HELP);
                    break;
            }
        }

    }

    private void sendRequest(final DuelPlayer sender, final DuelPlayer receiver) {
        plugin.getPlayerManager().getPrivateGameRequests().put(sender, receiver);
        openMenu(sender.getBukkitPlayer());

    }

    // adding a spawn to an arena, if the arena already has 2 spawns, replace the last one with the current location of the player
    private void addSpawn(final YamlConfig arenaYml, final String name, final Player player) {
        final Arena arena = plugin.getArenaManager().getArenaByName(name);
        if (plugin.getArenaManager().doesArenaExist(name)) {
            if (canAddSpawn(arena)) {
                arena.addSpawn(player.getLocation());
                arenaYml.getConfig().set("arenas." + name + ".spawns." + arena.getSpawns().size() + ".world", player.getLocation().getWorld().getName());
                arenaYml.getConfig().set("arenas." + name + ".spawns." + arena.getSpawns().size() + ".x", player.getLocation().getX());
                arenaYml.getConfig().set("arenas." + name + ".spawns." + arena.getSpawns().size() + ".y", player.getLocation().getY());
                arenaYml.getConfig().set("arenas." + name + ".spawns." + arena.getSpawns().size() + ".z", player.getLocation().getZ());
                arenaYml.getConfig().set("arenas." + name + ".spawns." + arena.getSpawns().size() + ".pitch", player.getLocation().getPitch());
                arenaYml.getConfig().set("arenas." + name + ".spawns." + arena.getSpawns().size() + ".yaw", player.getLocation().getYaw());
                tell(Messages.ADDED_SPAWN.replace("{arena}", name));
            } else {
                arena.getSpawns().remove(1); // remove the last entry (user tried to add more spawns then players) for example 3 spawns, 2 player arena
                arena.addSpawn(player.getLocation());
                arenaYml.getConfig().set("arenas." + name + ".spawns." + arena.getSpawns().size() + ".world", player.getLocation().getWorld().getName());
                arenaYml.getConfig().set("arenas." + name + ".spawns." + arena.getSpawns().size() + ".x", player.getLocation().getX());
                arenaYml.getConfig().set("arenas." + name + ".spawns." + arena.getSpawns().size() + ".y", player.getLocation().getY());
                arenaYml.getConfig().set("arenas." + name + ".spawns." + arena.getSpawns().size() + ".z", player.getLocation().getZ());
                arenaYml.getConfig().set("arenas." + name + ".spawns." + arena.getSpawns().size() + ".pitch", player.getLocation().getPitch());
                arenaYml.getConfig().set("arenas." + name + ".spawns." + arena.getSpawns().size() + ".yaw", player.getLocation().getYaw());

                tell("&cYou are creating too many spawnpoints for the size of this arena!\n" +
                        "Your last set spawn point has been replaced by this one.");
            }
            arenaYml.saveConfig();
        } else tell(Messages.ARENA_NOT_FOUND);
    }

    private void joinDuels(final Player player) { // add a player to the duel gamemode, load their stats, kill/win effects, give inventory, etc
        if (!plugin.getPlayerManager().getDuelPlayers().containsKey(player.getUniqueId())) {
            plugin.getPlayerManager().addDuelPlayer(player.getUniqueId());

            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            player.getInventory().clear();

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSqLiteManager().createPlayerData(player.getUniqueId(), player.getName()));
            Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getScoreboard().lobbyScoreboard(player), 10L);

            tell(Messages.JOINED_DUELS);
            plugin.getPlayerManager().getDuelPlayer(player.getUniqueId()).giveLobbyInv();

            // teleporting to the duels lobby
            player.teleport(new Location(
                    Bukkit.getWorld(plugin.getConfig().getString("spawn.world")),
                    plugin.getConfig().getDouble("spawn.x"),
                    plugin.getConfig().getDouble("spawn.y"),
                    plugin.getConfig().getDouble("spawn.z"),
                    (float) plugin.getConfig().getDouble("spawn.pitch"),
                    (float) plugin.getConfig().getDouble("spawn.yaw")));
        } else tell(Messages.ALREADY_IN_DUELS);

    }

    private void leaveDuels(final Player player) {
        if (plugin.getPlayerManager().getDuelPlayers().containsKey(player.getUniqueId())) {
            plugin.getPlayerManager().removeDuelPlayer(player.getUniqueId());
            player.getInventory().clear();
            tell(Messages.LEFT_DUELS);
        } else tell(Messages.NOT_IN_DUELS);

    }

    private boolean canAddSpawn(final Arena arena) {
        return 2 > arena.getSpawns().size();
    }

    private void openMenu(final Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 27, Utils.colorize("&aSelect a private duel type!"));

        for (GameType type : GameType.values()) {
            inventory.addItem(type.getIcon());
        }
        player.openInventory(inventory);
    }

    // checks to see if a private game invite is allowed to be sent or not
    private boolean canInvite(final Player playerSender, final Player playerReceived) {
        final DuelPlayer received = plugin.getPlayerManager().getDuelPlayer(playerReceived.getUniqueId());
        final DuelPlayer sender = plugin.getPlayerManager().getDuelPlayer(playerSender.getUniqueId());
        if (sender == null) { // check if hte sender is in duels
            playerSender.sendMessage(Utils.colorize("&cYou must be in duels (/join duels) to execute this command"));
            return false;
        } else if (received == null) { // check if the receiver exists/is in duels
            sender.sendMessage("&cThis player is invalid");
            return false;
        } else if (sender.getUuid().equals(received.getUuid())) { // check if you are not inviting yourself
            sender.sendMessage("&cYou can not invite yourself");
            return false;
        } else if (plugin.getPlayerManager().getPrivateGameRequests().containsKey(sender)) { // check if there are no current pending requests on your side
            sender.sendMessage("&cYou already have a pending duel request, wait for it to expire!");
            return false;
        } else if (sender.getState() != PlayerState.IN_LOBBY || received.getState() != PlayerState.IN_LOBBY) { // check if both are in lobby
            sender.sendMessage("&cYou or this user is not in the right state to send this user a duel!");
            return false;
        } else {
            return true;
        }

    }

}
