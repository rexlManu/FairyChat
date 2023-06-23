package de.rexlmanu.fairychat.plugin.core.user.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.core.user.UserFactory;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
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
import org.bukkit.scheduler.BukkitScheduler;

@Singleton
public class UserBukkitListener implements Listener {
  private final UserService userService;
  private final UserFactory userFactory;
  private final Plugin plugin;
  private final BukkitScheduler scheduler;
  private final Server server;

  @Inject
  public UserBukkitListener(
      UserService userService,
      UserFactory userFactory,
      JavaPlugin plugin,
      PluginManager pluginManager,
      BukkitScheduler scheduler,
      Server server) {
    this.userService = userService;
    this.userFactory = userFactory;
    this.plugin = plugin;
    this.scheduler = scheduler;
    this.server = server;

    pluginManager.registerEvents(this, plugin);
  }

  @EventHandler
  public void handlePlayerJoin(PlayerJoinEvent event) {
    this.userService.login(this.userFactory.createFromPlayer(event.getPlayer()));
  }

  @EventHandler
  public void handlePlayerQuit(PlayerQuitEvent event) {
    this.userService
        .findUserById(event.getPlayer().getUniqueId())
        .ifPresent(this.userService::logout);
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
