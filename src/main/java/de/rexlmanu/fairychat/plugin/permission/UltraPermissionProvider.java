package de.rexlmanu.fairychat.plugin.permission;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.UUID;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.UltraPermissionsAPI;
import me.TechsCode.UltraPermissions.storage.collection.GroupList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class UltraPermissionProvider implements PermissionProvider {

  private UltraPermissionsAPI ultraPermissionsAPI;

  @Inject
  public UltraPermissionProvider() {
    this.ultraPermissionsAPI = UltraPermissions.getAPI();
  }

  @Override
  public @Nullable String getGroup(@NotNull UUID playerId) {
    return this.ultraPermissionsAPI
        .getUsers()
        .uuid(playerId)
        .map(
            user -> {
              GroupList groups = user.getActiveGroups().bestToWorst();
              if (groups.isEmpty()) return null;
              return groups.get(0).getName();
            })
        .orElse(null);
  }
}
