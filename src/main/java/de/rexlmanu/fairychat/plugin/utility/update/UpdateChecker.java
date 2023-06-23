package de.rexlmanu.fairychat.plugin.utility.update;

import static de.rexlmanu.fairychat.plugin.Constants.VERSION_URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.FairyChatConfiguration;
import de.rexlmanu.fairychat.plugin.utility.annotation.PluginLogger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UpdateChecker {
  public record Release(String version, String url) {}

  private final JavaPlugin plugin;
  private final FairyChatConfiguration configuration;
  @PluginLogger private final Logger logger;

  public void fetchLatestVersion(Consumer<Release> consumer) {
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
    if (!this.configuration.checkForUpdates()) return;
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
}
