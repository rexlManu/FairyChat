package de.rexlmanu.fairychat.plugin.core.chatclear.redis;

import de.rexlmanu.fairychat.plugin.utility.ServerIdentity;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public record ChatClearData(ServerIdentity origin, @Nullable UUID targetId) {}
