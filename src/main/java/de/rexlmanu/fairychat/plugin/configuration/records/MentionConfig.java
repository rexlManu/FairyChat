package de.rexlmanu.fairychat.plugin.configuration.records;

import de.exlll.configlib.Comment;

public record MentionConfig(
    String format,
    @Comment("The format how the name should be triggered. Default is @username")
    String mentionNameFormat,
    @Comment("How the name should be formatted. Default is <sender_name>")
    String highlightFormat,
    String soundName,
    float soundVolume,
    float soundPitch) {}
