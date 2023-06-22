package de.rexlmanu.fairychat.plugin.core.privatemessaging.redis;

import de.rexlmanu.fairychat.plugin.utility.ServerIdentity;
import java.util.UUID;

public record PrivateMessageData(
    ServerIdentity origin, ServerIdentity destination, UUID senderId, UUID recipientId, String message) {}
