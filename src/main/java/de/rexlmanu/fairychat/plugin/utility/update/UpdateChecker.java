package de.rexlmanu.fairychat.plugin.utility.update;

import static de.rexlmanu.fairychat.plugin.Constants.VERSION_URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.utility.annotation.PluginLogger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UpdateChecker {
  public record Release(String version, String url) {}

  private final JavaPlugin plugin;
  private final Provider<PluginConfiguration> configurationProvider;
  @PluginLogger private final Logger logger;
  private final MiniMessage miniMessage;

  @Nullable private Release latestRelease;

  public void fetchLatestVersion(Consumer<Release> consumer) {
    if (this.latestRelease != null) {
      consumer.accept(this.latestRelease);
      return;
    }
    HttpClient client = HttpClient.newHttpClient();
    client
        .sendAsync(
            HttpRequest.newBuilder().uri(URI.create(VERSION_URL)).GET().build(),
            HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::body)
        .thenApply(
            body -> {
              JsonObject object = JsonParser.parseString(body).getAsJsonObject();
              String version = object.get("tag_name").getAsString();
              if (version.startsWith("v")) version = version.substring(1);
              String url = object.get("html_url").getAsString();
              return new Release(version, url);
            })
        .thenApply(release -> this.latestRelease = release)
        .thenAccept(consumer)
        // catch exceptions
        .exceptionally(
            throwable -> {
              this.logger.warning("Failed to fetch latest version of FairyChat.");
              return null;
            });
  }

  @SuppressWarnings("UnstableApiUsage")
  public void checkAndNotify() {
    if (!this.configurationProvider.get().checkForUpdates()) return;
    this.logger.info("Checking for updates...");
    this.fetchLatestVersion(
        release -> {
          if (!this.plugin.getPluginMeta().getVersion().equals(release.version())) {
            this.logger.warning(
                "A new version of FairyChat is available. You can download it at " + release.url());
          } else {
            this.logger.info("You are using the latest version of FairyChat.");
          }
        });
  }

  public void notifyPlayer(Player player) {
    if (!this.configurationProvider.get().checkForUpdates()) return;
    if (!player.isOp() && !player.hasPermission("fairychat.notify-update")) {
      return;
    }

    this.fetchLatestVersion(
        release -> {
          if (this.plugin.getPluginMeta().getVersion().equals(release.version())) {
            return;
          }

          player.sendMessage(
              this.miniMessage.deserialize(
                  this.configurationProvider.get().messages().updateNotification(),
                  Placeholder.parsed("url", release.url()),
                  Placeholder.parsed("version", release.version())));
        });
  }
}
