package de.rexlmanu.fairychat.plugin.command.argument;

import de.rexlmanu.fairychat.plugin.core.user.User;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import java.util.List;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.jetbrains.annotations.NotNull;

/** Resolves local and Redis users, with online names for command completion. */
public final class UserParser<C extends Source>
    implements ArgumentParser<C, User>, BlockingSuggestionProvider.Strings<C> {
  public static <C extends Source> ParserDescriptor<C, User> userParser() {
    return ParserDescriptor.of(new UserParser<>(), User.class);
  }

  @Override
  public @NotNull ArgumentParseResult<User> parse(
      @NotNull CommandContext<C> context, @NotNull CommandInput input) {
    String username = input.readString();
    return context
        .inject(UserService.class)
        .orElseThrow()
        .findUserByUsername(username)
        .map(ArgumentParseResult::success)
        .orElseGet(() -> ArgumentParseResult.failure(new UserParseException(username, context)));
  }

  @Override
  public @NotNull List<String> stringSuggestions(
      @NotNull CommandContext<C> context, @NotNull CommandInput input) {
    return context.inject(UserService.class).orElseThrow().onlineUsers().stream()
        .map(User::username)
        .filter(name -> !name.equalsIgnoreCase(context.sender().source().getName()))
        .toList();
  }

  public static final class UserParseException extends ParserException {
    private UserParseException(String input, CommandContext<?> context) {
      super(
          UserParser.class,
          context,
          Caption.of("argument.parse.failure.user"),
          CaptionVariable.of("input", input));
    }
  }
}
