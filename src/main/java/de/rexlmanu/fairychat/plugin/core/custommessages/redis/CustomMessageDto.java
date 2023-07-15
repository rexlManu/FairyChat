package de.rexlmanu.fairychat.plugin.core.custommessages.redis;

import de.rexlmanu.fairychat.plugin.utility.ServerIdentity;
import net.kyori.adventure.text.Component;

public record CustomMessageDto(ServerIdentity origin, Component component) {}
