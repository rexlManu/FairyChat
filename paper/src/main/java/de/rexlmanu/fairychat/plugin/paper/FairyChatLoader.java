package de.rexlmanu.fairychat.plugin.paper;

import com.google.gson.Gson;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class FairyChatLoader implements PluginLoader {
  @Override
  public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
    MavenLibraryResolver resolver = new MavenLibraryResolver();
    PluginLibraries pluginLibraries = this.load();
    pluginLibraries.asDependencies().forEach(resolver::addDependency);
    pluginLibraries.asRepositories().forEach(resolver::addRepository);
    classpathBuilder.addLibrary(resolver);
  }

  public PluginLibraries load() {
    try (var inputStream = this.getClass().getResourceAsStream("/paper-libraries.json")) {
      return new Gson()
          .fromJson(
              new InputStreamReader(inputStream, StandardCharsets.UTF_8), PluginLibraries.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private record PluginLibraries(Map<String, String> repositories, List<String> dependencies) {
    public Stream<Dependency> asDependencies() {
      return this.dependencies.stream().map(d -> new Dependency(new DefaultArtifact(d), null));
    }

    public Stream<RemoteRepository> asRepositories() {
      return this.repositories.entrySet().stream()
          .map(e -> new RemoteRepository.Builder(e.getKey(), "default", e.getValue()).build());
    }
  }
}
