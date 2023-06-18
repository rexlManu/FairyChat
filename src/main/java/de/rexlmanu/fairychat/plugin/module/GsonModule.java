package de.rexlmanu.fairychat.plugin.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.utility.ComponentTypeAdapter;
import net.kyori.adventure.text.Component;

public class GsonModule extends AbstractModule {
  @Provides
  @Singleton
  public Gson provideGson() {
    return new GsonBuilder()
        .serializeNulls()
        .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
        .enableComplexMapKeySerialization()
        .create();
  }
}
