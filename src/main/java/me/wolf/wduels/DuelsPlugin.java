package me.wolf.wduels;

import me.wolf.wduels.arena.ArenaManager;
import me.wolf.wduels.commands.DuelsCommand;
import me.wolf.wduels.game.GameHandler;
import me.wolf.wduels.listeners.*;
import me.wolf.wduels.managers.*;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.scoreboard.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;

public class DuelsPlugin extends JavaPlugin {

    private DuelsPlugin plugin;
    private ArenaManager arenaManager;
    private FileManager fileManager;
    private GameManager gameManager;
    private KitManager kitManager;
    private PlayerManager playerManager;
    private KillEffectManager killEffectManager;
    private WinEffectManager winEffectManager;
    private SQLiteManager sqLiteManager;
    private GameHandler gameHandler;
    private Scoreboard scoreboard;

    @Override
    public void onEnable() {
        this.plugin = this;

        this.getConfig().options().copyDefaults();
        saveDefaultConfig();

        registerManagers();
        registerCommands();
        registerListeners();


        Bukkit.getPluginManager().registerEvents(new BlockPlace(plugin), this);


    }

    @Override
    public void onDisable() {
        for (final DuelPlayer player : getPlayerManager().getDuelPlayers().values()) {
            sqLiteManager.saveData(player);
        }
        sqLiteManager.disconnect();
    }

    private void registerCommands() {
        Collections.singletonList(
                new DuelsCommand(this)
        ).forEach(this::registerCommand);

    }

    private void registerListeners() {
        Arrays.asList(
                new InventoryListeners(this),
                new BlockBreak(this),
                new BlockPlace(this),
                new EntityDamage(this),
                new InventoryInteracting(this),
                new PlayerQuit(this),
                new CombatEvents(this),
                new FoodLevel(this),
                new AcceptPrivateGame(this)
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    private void registerCommand(final Command command) {
        try {
            final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(command.getLabel(), command);

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    private void registerManagers() {
        this.sqLiteManager = new SQLiteManager(this);
        this.sqLiteManager.connect();

        this.fileManager = new FileManager(this);
        this.playerManager = new PlayerManager();
        this.arenaManager = new ArenaManager(this);
        this.gameManager = new GameManager(this);
        this.gameHandler = new GameHandler(this);
        this.kitManager = new KitManager(this);
        this.killEffectManager = new KillEffectManager();
        this.winEffectManager = new WinEffectManager(this);
        this.scoreboard = new Scoreboard(this);

        kitManager.loadKits(fileManager.getKitsConfigFile());
        arenaManager.loadArenas(fileManager.getArenasConfigFile());
        winEffectManager.loadWinEffects();
        killEffectManager.loadKillEffects();

    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }


    public GameManager getGameManager() {
        return gameManager;
    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public KillEffectManager getKillEffectManager() {
        return killEffectManager;
    }

    public WinEffectManager getWinEffectManager() {
        return winEffectManager;
    }

    public SQLiteManager getSqLiteManager() {
        return sqLiteManager;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

}
