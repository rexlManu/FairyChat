package de.rexlmanu.fairychat.plugin.core.broadcast;

import de.rexlmanu.fairychat.plugin.utility.ServerIdentity;
import net.kyori.adventure.text.Component;

public record BroadcastMessageData(ServerIdentity origin, Component message) {}
