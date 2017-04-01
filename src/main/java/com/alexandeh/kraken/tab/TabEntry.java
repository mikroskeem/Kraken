package com.alexandeh.kraken.tab;

import com.alexandeh.kraken.tab.event.FakePlayerEntry;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.UUID;

@Setter
@Getter
public class TabEntry {

    private PlayerTab playerTab;
    private int x, y;
    private String text;
    private FakePlayerEntry playerEntry;
    private Team team;
    private boolean setup;

    public TabEntry(PlayerTab playerTab, String text, int x, int y) {
        this.playerTab = playerTab;
        this.text = text;
        this.x = x;
        this.y = y;

        playerTab.getEntries().add(this);
    }

    private TabEntry setup() {
        setup = true;

        Player player = playerTab.getPlayer();
        playerEntry = new FakePlayerEntry(
                new WrappedGameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', text)),
                0,
                text
        );

        WrapperPlayServerPlayerInfo wpspi = new WrapperPlayServerPlayerInfo();
        wpspi.setAction(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
        wpspi.setData(Collections.singletonList(new PlayerInfoData(
                playerEntry.getProfile(),
                playerEntry.getLatency(),
                EnumWrappers.NativeGameMode.NOT_SET,
                WrappedChatComponent.fromText(playerEntry.getName())
        )));
        wpspi.sendPacket(player);

        team = playerTab.getScoreboard().registerNewTeam(UUID.randomUUID().toString().substring(0, 16));
        team.addEntry(playerEntry.getName());

        return this;
    }

    public TabEntry send() {
        if (!setup) {
            return setup();
        }

        text = ChatColor.translateAlternateColorCodes('&', text);

        if (text.length() > 16) {
            team.setPrefix(text.substring(0, 16));
            String suffix = ChatColor.getLastColors(team.getPrefix()) + text.substring(16, text.length());
            if (suffix.length() > 16) {
                if (suffix.length() <= 16) {
                    suffix = text.substring(16, text.length());
                    team.setSuffix(suffix.substring(0, suffix.length()));
                } else {
                    team.setSuffix(suffix.substring(0, 16));
                }
            } else {
                team.setSuffix(suffix);
            }
        } else {
            team.setPrefix(text);
            team.setSuffix("");
        }

        return this;
    }

}
