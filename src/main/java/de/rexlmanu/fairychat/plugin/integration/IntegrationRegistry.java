package de.rexlmanu.fairychat.plugin.integration;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.integration.chat.PlaceholderSupport;
import de.rexlmanu.fairychat.plugin.integration.types.BuiltInPlaceholdersIntegration;
import de.rexlmanu.fairychat.plugin.integration.types.DisplayItemChatIntegration;
import de.rexlmanu.fairychat.plugin.integration.types.LuckPermsIntegration;
import de.rexlmanu.fairychat.plugin.integration.types.MiniPlaceholdersIntegration;
import de.rexlmanu.fairychat.plugin.integration.types.PlaceholderAPIIntegration;
import de.rexlmanu.fairychat.plugin.integration.types.SpoilerChatIntegration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.PluginManager;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class IntegrationRegistry {
  private final Logger logger;
  private final Injector injector;
  private final List<Integration> integrations = new ArrayList<>();
  private final PluginManager pluginManager;

  public void init() {
    // first party integrations
    this.tryEnable(BuiltInPlaceholdersIntegration.class, () -> true);
    this.tryEnable(DisplayItemChatIntegration.class, () -> true);
    this.tryEnable(SpoilerChatIntegration.class, () -> true);

    // third party integrations
    this.tryEnable(
        PlaceholderAPIIntegration.class,
        () -> this.pluginManager.getPlugin(Constants.PLACEHOLDER_API_NAME) != null);
    this.tryEnable(
        MiniPlaceholdersIntegration.class,
        () -> this.pluginManager.getPlugin("MiniPlaceholders") != null);
    this.tryEnable(
        LuckPermsIntegration.class,
        () -> this.pluginManager.getPlugin(Constants.LUCKPERMS_NAME) != null);
  }

  private void tryEnable(
      Class<? extends Integration> integrationClass, Supplier<Boolean> availablePredicate) {
    if (!availablePredicate.get()) {
      return;
    }
    String integrationName = integrationClass.getSimpleName().replace("Integration", "");
    try {
      Integration integration = this.injector.getInstance(integrationClass);
      if (integration.available()) {
        integration.enable();
        this.integrations.add(integration);
        this.logger.info(() -> String.format("Enabled integration %s.", integrationName));
      }
    } catch (Exception exception) {
      this.logger.warning(
          () ->
              String.format(
                  "Failed to enable integration %s: %s", integrationName, exception.getMessage()));
    }
  }

  public List<PlaceholderSupport> getPlaceholderSupports() {
    return this.integrations.stream()
        .filter(PlaceholderSupport.class::isInstance)
        .map(PlaceholderSupport.class::cast)
        .toList();
  }
}
