package de.rexlmanu.fairychat.plugin.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import de.rexlmanu.fairychat.plugin.core.user.User;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UserArgument<C> extends CommandArgument<C, User> {

  private UserArgument(
      final boolean required,
      final @NotNull String name,
      final @NotNull String defaultValue,
      final @Nullable BiFunction<
                  @NotNull CommandContext<C>, @NotNull String, @NotNull List<@NotNull String>>
              suggestionsProvider,
      final @NotNull ArgumentDescription defaultDescription) {
    super(
        required,
        name,
        new UserParser<>(),
        defaultValue,
        User.class,
        suggestionsProvider,
        defaultDescription);
  }

  public static <C> @NotNull Builder<C> builder(final @NotNull String name) {
    return new Builder<>(name);
  }

  public static <C> @NotNull CommandArgument<C, User> of(final @NotNull String name) {
    return UserArgument.<C>builder(name).asRequired().build();
  }

  public static <C> @NotNull CommandArgument<C, User> optional(final @NotNull String name) {
    return UserArgument.<C>builder(name).asOptional().build();
  }

  public static <C> @NotNull CommandArgument<C, User> optional(
      final @NotNull String name, final @NotNull User defaultUser) {
    return UserArgument.<C>builder(name).asOptionalWithDefault(defaultUser.toString()).build();
  }

  public static final class Builder<C> extends CommandArgument.Builder<C, User> {

    private Builder(final @NotNull String name) {
      super(User.class, name);
    }

    @Override
    public @NotNull UserArgument<C> build() {
      return new UserArgument<>(
          this.isRequired(),
          this.getName(),
          this.getDefaultValue(),
          this.getSuggestionsProvider(),
          this.getDefaultDescription());
    }
  }

  public static final class UserParser<C> implements ArgumentParser<C, User> {

    @Override
    public @NotNull ArgumentParseResult<User> parse(
        @NotNull CommandContext<C> commandContext, @NotNull Queue<@NotNull String> inputQueue) {
      String input = inputQueue.peek();
      if (input == null) {
        return ArgumentParseResult.failure(
            new NoInputProvidedException(UserParser.class, commandContext));
      }

      try {
        User user =
            commandContext
                .inject(UserService.class)
                .orElseThrow()
                .findUserByUsername(input)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        inputQueue.remove();
        return ArgumentParseResult.success(user);
      } catch (IllegalArgumentException e) {
        return ArgumentParseResult.failure(new UserParseException(input, commandContext));
      }
    }

    @Override
    public @NonNull List<@NonNull String> suggestions(
        @NonNull CommandContext<C> commandContext, @NonNull String input) {
      return commandContext.inject(UserService.class).orElseThrow().onlineUsers().stream()
          .map(User::username)
          .toList();
    }

    @Override
    public boolean isContextFree() {
      return true;
    }
  }

  public static final class UserParseException extends ParserException {

    private final String input;

    public UserParseException(
        final @NotNull String input, final @NotNull CommandContext<?> context) {
      super(
          UserParser.class,
          context,
          Caption.of("argument.parse.failure.user"),
          CaptionVariable.of("input", input));
      this.input = input;
    }

    public String getInput() {
      return this.input;
    }
  }
}
