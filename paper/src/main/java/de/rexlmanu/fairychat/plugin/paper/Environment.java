package de.rexlmanu.fairychat.plugin.paper;

public enum Environment {
  SPIGOT,
  PAPER,
  UNKNOWN;

  public static final Environment ENVIRONMENT = detectEnvironment();

  public boolean isPaper() {
    return this == PAPER;
  }

  public boolean isSpigot() {
    return this == SPIGOT;
  }

  private static Environment detectEnvironment() {
    if (hasClass("com.destroystokyo.paper.PaperConfig")
        || hasClass("io.papermc.paper.configuration.Configuration")) {
      return PAPER;
    } else if (hasClass("org.spigotmc.SpigotConfig")) {
      return SPIGOT;
    } else {
      return UNKNOWN;
    }
  }

  private static boolean hasClass(String className) {
    try {
      Class.forName(className);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}
