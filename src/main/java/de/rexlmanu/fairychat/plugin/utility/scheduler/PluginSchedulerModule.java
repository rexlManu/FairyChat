package de.rexlmanu.fairychat.plugin.utility.scheduler;

import com.google.inject.AbstractModule;

public class PluginSchedulerModule extends AbstractModule {
  private static final String FOLIA_CLASS = "io.papermc.paper.threadedregions.RegionizedServer";
  private static boolean FOLIA = false;

  static {
    try {
      Class.forName(FOLIA_CLASS);
      FOLIA = true;
    } catch (ClassNotFoundException e) {
      FOLIA = false;
    }
  }

  @Override
  protected void configure() {
    this.bind(PluginScheduler.class)
        .to(FOLIA ? FoliaPluginScheduler.class : PaperPluginScheduler.class);
  }
}
