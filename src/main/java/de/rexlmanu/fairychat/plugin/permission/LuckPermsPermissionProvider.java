package de.rexlmanu.fairychat.plugin.permission;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Optional;
import java.util.UUID;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class LuckPermsPermissionProvider implements PermissionProvider {
  private final LuckPerms luckPerms;

  @Inject
  public LuckPermsPermissionProvider(Server server) {
    this.luckPerms = server.getServicesManager().load(LuckPerms.class);
  }

  @Override
  public @Nullable String getGroup(@NotNull UUID playerId) {
    return Optional.ofNullable(this.luckPerms.getUserManager().getUser(playerId))
        .map(User::getPrimaryGroup)
        .orElse(null);
  }
}
