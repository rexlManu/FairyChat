package de.rexlmanu.fairychat.plugin.integration;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.integration.chat.PlaceholderSupport;
import de.rexlmanu.fairychat.plugin.integration.types.BuiltInPlaceholdersIntegration;
import de.rexlmanu.fairychat.plugin.integration.types.DisplayItemChatIntegration;
import de.rexlmanu.fairychat.plugin.integration.types.LuckPermsIntegration;
import de.rexlmanu.fairychat.plugin.integration.types.MiniPlaceholdersIntegration;
import de.rexlmanu.fairychat.plugin.integration.types.PlaceholderAPIIntegration;
import de.rexlmanu.fairychat.plugin.integration.types.SpoilerChatIntegration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class IntegrationRegistry {
  private final Logger logger;
  private final Injector injector;
  private final List<Integration> integrations = new ArrayList<>();

  public void init() {
    // first party integrations
    this.tryEnable(BuiltInPlaceholdersIntegration.class);
    this.tryEnable(DisplayItemChatIntegration.class);
    this.tryEnable(SpoilerChatIntegration.class);

    // third party integrations
    this.tryEnable(PlaceholderAPIIntegration.class);
    this.tryEnable(MiniPlaceholdersIntegration.class);
    this.tryEnable(LuckPermsIntegration.class);
  }

  private void tryEnable(Class<? extends Integration> integrationClass) {
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
