package com.alexandeh.kraken;

import com.alexandeh.kraken.tab.PlayerTab;
import com.alexandeh.kraken.tab.event.PlayerTabRemoveEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;

public class Kraken implements Listener {
    @Getter private static Kraken instance;
    @Getter private JavaPlugin plugin;
    @Getter private KrakenOptions options;

    /**
     * Construct {@link Kraken} using default options
     * @param plugin Plugin to link tasks to
     */
    public Kraken(JavaPlugin plugin) {
        this(plugin, KrakenOptions.getDefaultOptions());
    }

    /**
     * Construct {@link Kraken} using {@link KrakenOptions}
     * @param plugin Plugin to link tasks to
     * @param options Options
     */
    public Kraken(JavaPlugin plugin, KrakenOptions options) {
        if (Bukkit.getMaxPlayers() < 60) {
            throw new RuntimeException("Player limit must be at least 60!");
        }
        instance = this;

        this.plugin = plugin;
        this.options = options;

        /* Check existing players */
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            plugin.getServer().getOnlinePlayers().forEach(this::checkPlayer);
        }, 4L);

        /* Register events */
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> checkPlayer(player), 4L);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerTab playerTab = PlayerTab.getByPlayer(player);
        if (playerTab != null) {
            new HashSet<>(playerTab.getScoreboard().getTeams()).forEach(Team::unregister);
            PlayerTab.getPlayerTabs().remove(playerTab);
            Bukkit.getPluginManager().callEvent(new PlayerTabRemoveEvent(playerTab));
        }
    }

    private void checkPlayer(Player player) {
        PlayerTab playerTab = PlayerTab.getByPlayer(player);
        if (playerTab == null) {
            long time = System.currentTimeMillis();
            new PlayerTab(player);
            if (options.sendCreationMessage()) {
                player.sendMessage(ChatColor.BLUE + "Tab created in " + (System.currentTimeMillis() - time) + " ms.");
            }
        } else {
            playerTab.clear();
        }
    }
}
