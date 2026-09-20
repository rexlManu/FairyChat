package de.rexlmanu.fairychat.plugin.command.argument;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.rexlmanu.fairychat.plugin.core.user.User;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import de.rexlmanu.fairychat.plugin.utility.ServerIdentity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.paper.util.sender.Source;
import org.junit.jupiter.api.Test;

class UserParserTest {
  private final UserService users = mock(UserService.class);
  private final UserParser<Source> parser = new UserParser<>();

  @SuppressWarnings("unchecked")
  private final CommandContext<Source> context = mock(CommandContext.class);

  private User user(String name) {
    return new User(UUID.randomUUID(), name, mock(ServerIdentity.class));
  }

  @Test
  void resolvesUserAndLeavesMessageForNextArgument() {
    User recipient = user("Alex");
    when(context.inject(UserService.class)).thenReturn(Optional.of(users));
    when(users.findUserByUsername("Alex")).thenReturn(Optional.of(recipient));
    CommandInput input = CommandInput.of("Alex hello there");
    assertEquals(Optional.of(recipient), parser.parse(context, input).parsedValue());
    assertEquals(" hello there", input.remainingInput());
  }

  @Test
  void missingUserReturnsAParseFailure() {
    when(context.inject(UserService.class)).thenReturn(Optional.of(users));
    when(users.findUserByUsername("missing")).thenReturn(Optional.empty());
    var result = parser.parse(context, CommandInput.of("missing"));
    assertTrue(result.parsedValue().isEmpty());
    assertInstanceOf(UserParser.UserParseException.class, result.failure().orElseThrow());
  }

  @Test
  void suggestionsIncludeRemoteUsersAndExcludeTheSenderIgnoringCase() {
    Source source = mock(Source.class);
    CommandSender sender = mock(CommandSender.class);
    when(context.sender()).thenReturn(source);
    when(source.source()).thenReturn(sender);
    when(sender.getName()).thenReturn("ALEX");
    when(context.inject(UserService.class)).thenReturn(Optional.of(users));
    when(users.onlineUsers()).thenReturn(List.of(user("Alex"), user("Steve")));
    assertEquals(List.of("Steve"), parser.stringSuggestions(context, CommandInput.of("")));
  }
}
