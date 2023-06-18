package de.rexlmanu.fairychat.plugin.redis.data;

import java.util.UUID;
import net.kyori.adventure.text.Component;

public record BroadcastMessageData(UUID serverId, Component message) {}
