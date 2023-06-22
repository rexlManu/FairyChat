package de.rexlmanu.fairychat.plugin.core.user.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.core.user.UserFactory;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Singleton
public class UserBukkitListener implements Listener {
  private final UserService userService;
  private final UserFactory userFactory;

  @Inject
  public UserBukkitListener(
      UserService userService,
      UserFactory userFactory,
      JavaPlugin plugin,
      PluginManager pluginManager) {
    this.userService = userService;
    this.userFactory = userFactory;

    pluginManager.registerEvents(this, plugin);
  }

  @EventHandler
  public void handlePlayerJoin(PlayerJoinEvent event) {
    this.userService.login(this.userFactory.createFromPlayer(event.getPlayer()));
  }

  @EventHandler
  public void handlePlayerQuit(PlayerQuitEvent event) {
    this.userService.logout(this.userFactory.createFromPlayer(event.getPlayer()));
  }
}
