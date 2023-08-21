package de.rexlmanu.fairychat.plugin.permission;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.utility.annotation.PluginLogger;
import java.util.logging.Logger;
import org.bukkit.Server;

public class PermissionModule extends AbstractModule {
  @Provides
  public PermissionProvider providePermissionProvider(
      Server server, Injector injector, @PluginLogger Logger logger) {
    if (server.getPluginManager().getPlugin(Constants.LUCKPERMS_NAME) != null) {
      return injector.getInstance(LuckPermsPermissionProvider.class);
    }
    if (server.getPluginManager().getPlugin(Constants.ULTRAPERMISSIONS_NAME) != null) {
      return injector.getInstance(UltraPermissionProvider.class);
    }
    logger.warning("No permission provider found, using dummy provider.");
    return new DummyPermissionProvider();
  }
}
