package de.rexlmanu.fairychat.plugin.core.user.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.core.user.UserFactory;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Singleton
public class UserBukkitListener implements Listener {
  // After how much time a player is logged out after leaving the server.
  // This is to prevent the race condition on server switching.
  private static final long LOGOUT_TIMEOUT_MILLIS = 5L * Ticks.SINGLE_TICK_DURATION_MS;
  private final UserService userService;
  private final UserFactory userFactory;
  private final Plugin plugin;
  private final Server server;

  @Inject
  public UserBukkitListener(
      UserService userService,
      UserFactory userFactory,
      JavaPlugin plugin,
      PluginManager pluginManager,
      Server server) {
    this.userService = userService;
    this.userFactory = userFactory;
    this.plugin = plugin;
    this.server = server;

    pluginManager.registerEvents(this, plugin);
  }

  @EventHandler
  public void handlePlayerJoin(PlayerJoinEvent event) {
    this.userService.login(this.userFactory.createFromPlayer(event.getPlayer()));
  }

  @EventHandler
  public void handlePlayerQuit(PlayerQuitEvent event) {
    this.server
        .getAsyncScheduler()
        .runDelayed(
            this.plugin,
            (task) ->
                this.userService
                    .findUserById(event.getPlayer().getUniqueId())
                    .filter(user -> user.serverIdentity().equals(Constants.SERVER_IDENTITY_ORIGIN))
                    .filter(user -> this.server.getPlayer(user.uniqueId()) == null)
                    .ifPresent(this.userService::logout),
            LOGOUT_TIMEOUT_MILLIS,
            TimeUnit.MILLISECONDS);
  }

  @EventHandler
  public void handle(PluginEnableEvent event) {
    if (event.getPlugin().getName().equals("FairyChat")) {
      this.server
          .getOnlinePlayers()
          .forEach(player -> this.userService.login(this.userFactory.createFromPlayer(player)));
    }
  }

  @EventHandler
  public void handle(PluginDisableEvent event) {
    if (event.getPlugin().getName().equals("FairyChat")) {
      this.server
          .getOnlinePlayers()
          .forEach(player -> this.userService.logout(this.userFactory.createFromPlayer(player)));
    }
  }
}
