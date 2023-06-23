package de.rexlmanu.fairychat.plugin.core.user.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.core.user.UserFactory;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

  @Inject
  public UserBukkitListener(
      UserService userService,
      UserFactory userFactory,
      JavaPlugin plugin,
      PluginManager pluginManager,
      BukkitScheduler scheduler) {
    this.userService = userService;
    this.userFactory = userFactory;
    this.plugin = plugin;
    this.scheduler = scheduler;

    pluginManager.registerEvents(this, plugin);
  }

  @EventHandler
  public void handlePlayerJoin(PlayerJoinEvent event) {
    this.scheduler.runTaskLaterAsynchronously(
        this.plugin,
        () -> this.userService.login(this.userFactory.createFromPlayer(event.getPlayer())),
        500);
  }

  @EventHandler
  public void handlePlayerQuit(PlayerQuitEvent event) {
    this.userService
        .findUserById(event.getPlayer().getUniqueId())
        .ifPresent(this.userService::logout);
  }
}
