package com.alexandeh.kraken.tab.event;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Mark Vainomaa
 */
@RequiredArgsConstructor
@Getter
public class FakePlayerEntry {
    private final WrappedGameProfile profile; // = new WrappedGameProfile(UUID.randomUUID(), "playerName");
    private final int latency; // iow ping
    private final String name; // = WrappedChatComponent.fromText("playerName");
}
