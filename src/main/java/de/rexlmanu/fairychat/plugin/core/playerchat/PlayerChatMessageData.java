package de.rexlmanu.fairychat.plugin.core.playerchat;

import de.rexlmanu.fairychat.plugin.utility.ServerIdentity;
import java.util.UUID;
import net.kyori.adventure.text.Component;

public record PlayerChatMessageData(ServerIdentity origin, UUID senderId, Component message) {}
