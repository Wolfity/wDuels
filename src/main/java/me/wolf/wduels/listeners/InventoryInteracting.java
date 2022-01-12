package me.wolf.wduels.listeners;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.arena.Arena;
import me.wolf.wduels.game.Game;
import me.wolf.wduels.game.GameState;
import me.wolf.wduels.game.GameType;
import me.wolf.wduels.game.games.PrivateGame;
import me.wolf.wduels.killeffects.KillEffect;
import me.wolf.wduels.managers.GameManager;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.utils.Utils;
import me.wolf.wduels.wineffects.WinEffect;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InventoryInteracting implements Listener {

    private final DuelsPlugin plugin;


    public InventoryInteracting(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onOpenMenu(PlayerInteractEvent event) { // menus
        if (!plugin.getPlayerManager().getDuelPlayers().containsKey(event.getPlayer().getUniqueId())) return;
        final DuelPlayer player = plugin.getPlayerManager().getDuelPlayer(event.getPlayer().getUniqueId());
        final ItemStack clickedItem = event.getItem();

        if (clickedItem == null) return;
        if (clickedItem.getItemMeta() == null) return;

        if (clickedItem.getType() == Material.DIAMOND_SWORD) { // the queue menu opens, you can select a gamemode  to queue for
            if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.colorize("&aDuels &7(Click to queue)"))) {
                openMenu(event.getPlayer());
            }
        } else if (clickedItem.getType() == Material.BARRIER) { // if you are in a queue, you can dequeue by clicking
            if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.colorize("&cClick To Dequeue"))) {
                plugin.getGameManager().handleGameLeave(plugin.getGameManager().getGameByPlayer(player), player);
                player.sendMessage("&cLeft the queue!");
            }
        } else if (clickedItem.getType() == Material.BLAZE_POWDER) { // select kill effects by clicking
            if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.colorize("&aKill Effects &7(Click to open)"))) {
                openKillEffectMenu(event.getPlayer());
            }

        } else if (clickedItem.getType() == Material.FIREWORK) { // select win effects by clicking
            if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.colorize("&aWin Effects &7(Click to queue)"))) {
                openWinEffectMenu(player.getBukkitPlayer());
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQueue(InventoryClickEvent event) { // this is inside the queue menu
        if (!plugin.getPlayerManager().getDuelPlayers().containsKey(event.getWhoClicked().getUniqueId())) return;
        if (!event.getInventory().getName().equalsIgnoreCase(Utils.colorize("&aSelect a duel type!"))) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;

        final GameManager gameManager = plugin.getGameManager();
        final DuelPlayer duelPlayer = plugin.getPlayerManager().getDuelPlayer(event.getWhoClicked().getUniqueId());


        for (final GameType gameType : GameType.values()) {
            if (gameType.getIcon().equals(event.getCurrentItem())) { // check if the clicked item represents a gamemode icon
                if (gameManager.isPlayerInQueue(duelPlayer)) { // dequeue the player if he already is in a queue
                    final Game game = plugin.getGameManager().getGameByPlayer(duelPlayer);
                    gameManager.handleGameLeave(game, duelPlayer); // handling as a leave, so the game gets stopped and removed, so that there won't be a bunch of dead/unused game objects
                    duelPlayer.sendMessage("&cYou were already in a queue and are now dequeued!");
                    if (game.isPrivate()) { // cancel the invite
                        ((PrivateGame) game).cancelTimer();
                    }

                } else if (gameType != GameType.UHC) {
                    if (gameManager.getFreeGameByGameType(gameType) != null) { // there is an available game, add to queue
                        final Game availableGame = gameManager.getFreeGameByGameType(gameType);
                        gameManager.joinQueue(availableGame, duelPlayer);

                    } else {
                        if (plugin.getArenaManager().getFreeArena() != null) { // there is an available arena, get it, and pass it to a game that is recruiting
                            final Arena arena = plugin.getArenaManager().getFreeArena();
                            final Game game = gameManager.createGame(arena, gameType, false);
                            plugin.getGameHandler().setGameState(game, GameState.QUEUE);
                            gameManager.joinQueue(game, duelPlayer); // add user to queue

                        } else duelPlayer.sendMessage("&cNo available arenas!");
                    }

                } else { // UHC (You can break blocks/place blocks)
                    if (gameManager.getFreeGameByGameType(GameType.UHC) != null) { // there is an available game with open queue
                        final Game availableGame = gameManager.getFreeGameByGameType(GameType.UHC);
                        gameManager.joinQueue(availableGame, duelPlayer);
                    } else {
                        if (plugin.getArenaManager().getFreeArena() != null) { // free arena + no available queue, create new game
                            final Arena arena = plugin.getArenaManager().getFreeArena();
                            final Game game = gameManager.createGame(arena, GameType.UHC, false);
                            plugin.getGameHandler().setGameState(game, GameState.QUEUE);
                            gameManager.joinQueue(game, duelPlayer);

                        } else duelPlayer.sendMessage("&cNo available arenas!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSelectKillEffect(InventoryClickEvent event) { // kill effect menu
        if (!plugin.getPlayerManager().getDuelPlayers().containsKey(event.getWhoClicked().getUniqueId())) return;
        if (!event.getInventory().getName().equalsIgnoreCase(Utils.colorize("&aSelect a Kill Effect"))) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;

        final DuelPlayer duelPlayer = plugin.getPlayerManager().getDuelPlayer(event.getWhoClicked().getUniqueId());

        plugin.getKillEffectManager().getKillEffects().forEach(killEffect -> {
            // loop over all effects, check if the locked item is clicked or unlocked, if unlocked, select, else send msg informing the user has invalid perms
            if (event.getCurrentItem().equals(killEffect.getIconLocked())) {
                duelPlayer.getBukkitPlayer().playSound(duelPlayer.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5f, 0.5f);
                duelPlayer.sendMessage("&cYou do not have this kill effect unlocked!");

            } else if (event.getCurrentItem().equals(killEffect.getIconUnlocked())) {
                duelPlayer.getBukkitPlayer().playSound(duelPlayer.getLocation(), Sound.NOTE_PLING, 1f, 1f);
                duelPlayer.setKillEffect(killEffect);
                duelPlayer.sendMessage("&aNow selected the &2" + killEffect.getName() + "&a Kill Effect!");
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSqLiteManager().setKillEffect(duelPlayer.getUuid(), killEffect.getName()));
            }
        });
    }

    @EventHandler
    public void onSelectWinEffect(InventoryClickEvent event) {
        if (!plugin.getPlayerManager().getDuelPlayers().containsKey(event.getWhoClicked().getUniqueId())) return;
        if (!event.getInventory().getName().equalsIgnoreCase(Utils.colorize("&aSelect a Win Effect"))) return;
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;

        final DuelPlayer duelPlayer = plugin.getPlayerManager().getDuelPlayer(event.getWhoClicked().getUniqueId());

        // loop over all effects, check if the locked item is clicked or unlocked, if unlocked, select, else send message informing there are no perms
        plugin.getWinEffectManager().getWinEffects().forEach(winEffect -> {
            if (event.getCurrentItem().equals(winEffect.getIconLocked())) {
                duelPlayer.getBukkitPlayer().playSound(duelPlayer.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5f, 0.5f);
                duelPlayer.sendMessage("&cYou do not have this kill effect unlocked!");

            } else if (event.getCurrentItem().equals(winEffect.getIconUnlocked())) {
                duelPlayer.getBukkitPlayer().playSound(duelPlayer.getLocation(), Sound.NOTE_PLING, 1f, 1f);
                duelPlayer.setWinEffect(winEffect);
                duelPlayer.sendMessage("&aNow selected the &2" + winEffect.getName() + "&a Win Effect!");
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSqLiteManager().setWinEffect(duelPlayer.getUuid(), winEffect.getName()));

            }
        });
    }

    @EventHandler
    public void onPrivateGame(InventoryClickEvent event) {
        if (!plugin.getPlayerManager().getDuelPlayers().containsKey(event.getWhoClicked().getUniqueId())) return;
        if (!event.getInventory().getName().equalsIgnoreCase(Utils.colorize("&aSelect a private duel type!"))) return;
        event.setCancelled(true);
        if (event.getInventory() == null) return;
        if (event.getCurrentItem() == null) return;

        final GameManager gameManager = plugin.getGameManager();
        final DuelPlayer sender = plugin.getPlayerManager().getDuelPlayer(event.getWhoClicked().getUniqueId());

        for (final GameType gameType : GameType.values()) {
            if (gameType.getIcon().equals(event.getCurrentItem())) { // checking if the clicked item is a game icon (to create the game object + gametype)
                if (gameManager.isPlayerInQueue(sender)) {
                    gameManager.handleGameLeave(gameManager.getGameByPlayer(sender), sender);
                    sender.sendMessage("&cYou were already in a queue and are now dequeued!");
                } else {
                    // we will start looking for free arenas, if non, simply tell them theres no available arena. No need to check for an open queue (since its private)
                    if (plugin.getArenaManager().getFreeArena() == null) { // no available arenas
                        sender.sendMessage("&cNo available arenas!");

                    } else { // available arena, create a new private game
                        final Game game = gameManager.createGame(plugin.getArenaManager().getFreeArena(), gameType, true);

                        gameManager.joinQueue(game, sender);
                        sender.sendMessage("&bA Private Game instance for the game &3" + gameType.getIdentifier() + " &bhas been created!\n&aAwaiting a response.");

                        final DuelPlayer receiver = plugin.getPlayerManager().getPrivateGameRequests().get(sender);
                        sendClickableRequest(receiver, game, sender);

                    }
                    event.getWhoClicked().closeInventory();
                }
            }
        }
    }

    private void sendClickableRequest(final DuelPlayer receiver, final Game game, final DuelPlayer sender) {
        final TextComponent request = new TextComponent(Utils.colorize(
                "&2" + sender.getName() + " &ainvited you to a duel in &2" + game.getGameType().getIdentifier() + " &7(Click to accept)"));
        request.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duels accept " + sender.getName() + " " + game.getGameType().getIdentifier()));
        // sending a clickable text component that will run a command if clicked
        final PrivateGame priv = ((PrivateGame) game);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!priv.isAccepted()) {
                    if (priv.getInviteExpire() > 0) { // setting an expiration timer on the invite to invalidate it
                        priv.decrementInviteExpire();
                    } else { // invite expired, invalidate the invite
                        this.cancel();
                        priv.resetTimer(30);
                        sender.sendMessage("&cYour duel invite has expired");
                        plugin.getGameManager().handleGameLeave(game, sender);
                        plugin.getPlayerManager().getPrivateGameRequests().remove(sender);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

        receiver.getBukkitPlayer().spigot().sendMessage(request);
    }

    private void openMenu(final Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 27, Utils.colorize("&aSelect a duel type!"));

        for (GameType type : GameType.values()) {
            inventory.addItem(type.getIcon());
        }
        player.openInventory(inventory);
    }


    private void openKillEffectMenu(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 9, Utils.colorize("&aSelect a Kill Effect"));
        final List<KillEffect> sortedEffects = new ArrayList<>(plugin.getKillEffectManager().getKillEffects());

        sortedEffects.sort(Comparator.comparing(KillEffect::getName)); // sort by name

        for (final KillEffect killEffect : sortedEffects) {
            if (killEffect.getName().equalsIgnoreCase("default")) {
                inv.addItem(killEffect.getIconUnlocked());
            } else if (player.hasPermission(killEffect.getPermission())) {
                inv.addItem(killEffect.getIconUnlocked());
            } else inv.addItem(killEffect.getIconLocked());
        }

        player.openInventory(inv);
    }

    private void openWinEffectMenu(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 9, Utils.colorize("&aSelect a Win Effect"));
        final List<WinEffect> winEffects = new ArrayList<>(plugin.getWinEffectManager().getWinEffects());

        winEffects.sort(Comparator.comparing(WinEffect::getName)); // sort by name

        for (final WinEffect winEffect : winEffects) {
            if (winEffect.getName().equalsIgnoreCase("default")) {
                inv.addItem(winEffect.getIconUnlocked());
            } else if (player.hasPermission(winEffect.getPermission())) {
                inv.addItem(winEffect.getIconUnlocked());
            } else inv.addItem(winEffect.getIconLocked());
        }

        player.openInventory(inv);
    }

}
