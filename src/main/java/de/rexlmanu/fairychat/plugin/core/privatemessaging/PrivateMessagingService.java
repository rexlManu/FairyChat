package de.rexlmanu.fairychat.plugin.core.privatemessaging;

import de.rexlmanu.fairychat.plugin.core.user.User;
import java.util.Optional;

public interface PrivateMessagingService {
  void sendMessage(User user, User recipient, String message);

  Optional<User> lastRecipient(User user);
}
