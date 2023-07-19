package de.rexlmanu.fairychat.plugin.configuration.records;

import de.exlll.configlib.Comment;

public record RedisCredentials(
    @Comment({
          "Specifies whether Redis support should be enabled.",
          "When enabled, chat messages, broadcasts, and private messages will be handled over Redis."
        })
        boolean enabled,
    String url) {}
