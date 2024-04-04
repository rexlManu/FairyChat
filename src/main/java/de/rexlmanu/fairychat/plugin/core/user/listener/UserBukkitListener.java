package de.rexlmanu.fairychat.plugin.core.user.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.core.user.UserFactory;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import de.rexlmanu.fairychat.plugin.paper.PluginScheduler;
import de.rexlmanu.fairychat.plugin.utility.update.UpdateChecker;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Singleton
public class UserBukkitListener implements Listener {
  private final UserService userService;
  private final UserFactory userFactory;
  private final Server server;
  private final PluginScheduler pluginScheduler;
  private final UpdateChecker updateChecker;

  @Inject
  public UserBukkitListener(
      UserService userService,
      UserFactory userFactory,
      JavaPlugin plugin,
      PluginManager pluginManager,
      Server server,
      PluginScheduler pluginScheduler,
      UpdateChecker updateChecker) {
    this.userService = userService;
    this.userFactory = userFactory;
    this.server = server;
    this.pluginScheduler = pluginScheduler;
    this.updateChecker = updateChecker;

    pluginManager.registerEvents(this, plugin);
  }

  @EventHandler
  public void handlePlayerJoin(PlayerJoinEvent event) {
    this.userService.login(this.userFactory.createFromPlayer(event.getPlayer()));

    this.updateChecker.notifyPlayer(event.getPlayer());
  }

  @EventHandler
  public void handlePlayerQuit(PlayerQuitEvent event) {
    this.pluginScheduler.runAsync(
        () ->
            this.userService
                .findUserById(event.getPlayer().getUniqueId())
                .filter(user -> user.serverIdentity().equals(Constants.SERVER_IDENTITY_ORIGIN))
                .ifPresent(this.userService::logout));
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
