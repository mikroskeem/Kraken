package com.alexandeh.kraken.tab;


import com.alexandeh.kraken.Kraken;
import com.alexandeh.kraken.tab.event.FakePlayerEntry;
import com.alexandeh.kraken.tab.event.PlayerTabCreateEvent;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

@Getter
public class PlayerTab {
    @Getter private static Set<PlayerTab> playerTabs = new HashSet<>();
    private Player player;
    private Scoreboard scoreboard;
    private List<TabEntry> entries = new ArrayList<>();

    public PlayerTab(Player player) {
        this.player = player;
        clear();

        if (!player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            scoreboard = player.getScoreboard();
            assemble();
        } else {
            Kraken.getInstance().getPlugin().getServer().getScheduler().runTask(
                    Kraken.getInstance().getPlugin(),
                    () -> {
                        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                        player.setScoreboard(scoreboard);
                        assemble();
                    }
            );
        }
        playerTabs.add(this);
    }

    /**
     * Clear player's tab
     */
    public void clear() {
        /* Remove all entries */
        for (TabEntry entry : entries) {
            if (entry.playerEntry() != null) {
                FakePlayerEntry playerEntry = entry.playerEntry();
                WrapperPlayServerPlayerInfo wpspi = new WrapperPlayServerPlayerInfo();
                wpspi.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                wpspi.setData(Collections.singletonList(new PlayerInfoData(
                        playerEntry.getProfile(),
                        playerEntry.getLatency(),
                        EnumWrappers.NativeGameMode.NOT_SET,
                        WrappedChatComponent.fromText(playerEntry.getName())
                )));
                wpspi.sendPacket(player);
            }
        }

        /* Not sure what's that for */
        for (Player online : Bukkit.getOnlinePlayers()) {
            WrapperPlayServerPlayerInfo wpspi = new WrapperPlayServerPlayerInfo();
            wpspi.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            wpspi.setData(Collections.singletonList(new PlayerInfoData(
                    WrappedGameProfile.fromPlayer(online),
                    player.spigot().getPing(),
                    EnumWrappers.NativeGameMode.fromBukkit(online.getGameMode()),
                    WrappedChatComponent.fromText(online.getName())
            )));
            wpspi.sendPacket(player);
        }

        entries.clear();
    }

    private void assemble() {
        for (int i = 0; i < 60; i++) {
            int x = i % 3;
            int y = i / 3;
            new TabEntry(this, getNextBlank(), x, y).send();
        }

        Bukkit.getPluginManager().callEvent(new PlayerTabCreateEvent(this));
    }

    public TabEntry getByPosition(int x, int y) {
        return entries.stream()
                .filter(tab -> tab.x() == x && tab.x() == y)
                .findFirst()
                .orElse(null);
    }

    public String getNextBlank() {
        outer: for (String string : getAllBlanks()) {
            for (TabEntry tabEntry : entries) {
                if (tabEntry.text() != null && tabEntry.text().startsWith(string)) {
                    continue outer;
                }
            }
            return string;
        }
        return null;
    }

    private static List<String> getAllBlanks() {
        List<String> toReturn = new ArrayList<>();
        for (ChatColor chatColor : ChatColor.values()) {
            toReturn.add(chatColor + "" + ChatColor.RESET);
            for (ChatColor chatColor1 : ChatColor.values()) {
                if (toReturn.size() >= 60)
                    return toReturn;
                toReturn.add(chatColor + "" + chatColor1 + ChatColor.RESET);
            }
        }

        return toReturn;
    }

    public static PlayerTab getByPlayer(Player player) {
        return playerTabs.stream()
                .filter(tab -> tab.getPlayer().getUniqueId().equals(player.getUniqueId()))
                .findFirst()
                .orElse(null);
    }
}
