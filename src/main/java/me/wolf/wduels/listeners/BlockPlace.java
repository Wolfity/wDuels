package me.wolf.wduels.listeners;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.game.Game;
import me.wolf.wduels.game.GameType;
import me.wolf.wduels.game.games.ModifiableGame;
import me.wolf.wduels.game.games.PrivateModifiableGame;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.player.PlayerState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class BlockPlace implements Listener {

    private final DuelsPlugin plugin;

    public BlockPlace(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {

        final Player player = event.getPlayer();
        if (!plugin.getPlayerManager().getDuelPlayers().containsKey(player.getUniqueId())) return;

        final DuelPlayer duelPlayer = plugin.getPlayerManager().getDuelPlayer(player.getUniqueId());
        final Material placedMat = event.getBlock().getType();
        if (duelPlayer.getState() != PlayerState.IN_GAME) {
            event.setCancelled(true);
        }
        final Game game = plugin.getGameManager().getGameByPlayer(duelPlayer);
        if (game == null) return;
        // cancel all block place events, if you are not ingame, if you are in UHC you can place blocks, but only water, lava, wood, cobble
        switch (game.getGameType()) {
            case UHC:
                if (placedMat == Material.WOOD || placedMat == Material.COBBLESTONE) {
                    if(game.isPrivate()) {
                        ((PrivateModifiableGame) game).getPlacedBlocks().add(event.getBlock().getLocation());
                    } else ((ModifiableGame) game).getPlacedBlocks().add(event.getBlock().getLocation());
                } else {
                    event.setCancelled(true);
                }
                break;
            case COMBO:
            case ARCHER:
            case DEBUFF:
            case NO_DEBUFF:
            case CLASSIC:
            case DIAMOND:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) { // also add lava/water blocks that are placed in UHC to the list to remove
        if (plugin.getPlayerManager().getDuelPlayer(event.getPlayer().getUniqueId()) == null) return;
        final Game game = plugin.getGameManager().getGameByPlayer(plugin.getPlayerManager().getDuelPlayer(event.getPlayer().getUniqueId()));
        if (game.getGameType() == GameType.UHC) {
            if (!game.isPrivate()) {
                ((ModifiableGame) game).getPlacedBlocks().add(getPlacedLocation(event.getBlockClicked().getLocation(), event.getBlockFace()));
            } else ((PrivateModifiableGame) game).getPlacedBlocks().add(getPlacedLocation(event.getBlockClicked().getLocation(), event.getBlockFace()));
        }
    }

    private Location getPlacedLocation(Location l, BlockFace b) {
        int x = (b == BlockFace.WEST) ? -1 : ((b == BlockFace.EAST) ? 1 : 0), y = (b == BlockFace.DOWN) ? -1 : ((b == BlockFace.UP) ? 1 : 0), z = (b == BlockFace.SOUTH) ? 1 : ((b == BlockFace.NORTH) ? -1 : 0);
        return new Location(l.getWorld(), (l.getBlockX() + x), (l.getBlockY() + y), (l.getBlockZ() + z));
    }


}
