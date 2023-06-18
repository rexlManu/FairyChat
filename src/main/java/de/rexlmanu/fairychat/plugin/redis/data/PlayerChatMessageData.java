package de.rexlmanu.fairychat.plugin.redis.data;

import java.util.UUID;
import net.kyori.adventure.text.Component;

public record PlayerChatMessageData(UUID serverId, UUID senderId, Component message) {}
