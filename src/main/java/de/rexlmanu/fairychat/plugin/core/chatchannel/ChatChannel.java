package de.rexlmanu.fairychat.plugin.core.chatchannel;

import de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.strategy.CooldownStrategyType;
import java.util.List;
import java.util.Map;

public class ChatChannel {
  private String name;
  private boolean defaultChannel;
  private String mentionName;
  private boolean enterable;
  private boolean allowMultipleChannelsEntering;
  private String enterPermission;
  private String messageViewPermission;
  private String sentMessagePermission;
  private Map<String, String> formats;
  private CooldownStrategyType cooldownStrategyType;
  private ServerFilterMode filterMode;
  private List<String> serverList;
  private boolean localOnly;
    
}
