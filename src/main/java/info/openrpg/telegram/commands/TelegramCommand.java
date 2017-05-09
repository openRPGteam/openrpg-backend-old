package info.openrpg.telegram.commands;

import info.openrpg.constants.Commands;
import info.openrpg.telegram.commands.actions.DoNothingCommand;
import info.openrpg.telegram.commands.actions.ExecutableCommand;
import info.openrpg.telegram.commands.actions.HelpCommand;
import info.openrpg.telegram.commands.actions.PeekPlayerCommand;
import info.openrpg.telegram.commands.actions.PlayerInfoCommand;
import info.openrpg.telegram.commands.actions.SendMessageCommand;
import info.openrpg.telegram.commands.actions.StartCommand;
import lombok.AllArgsConstructor;

import javax.persistence.EntityManager;
import java.util.function.Function;
import java.util.stream.Stream;

@AllArgsConstructor
public enum TelegramCommand {
    NOTHING(Commands.NO_COMMAND, DoNothingCommand::new),
    START(Commands.START, StartCommand::new),
    HELP(Commands.HELP, HelpCommand::new),
    PLAYER_INFO(Commands.PLAYER_INFO, PlayerInfoCommand::new),
    PEEK_PLAYER(Commands.PEEK_PLAYER, PeekPlayerCommand::new),
    SEND_MESSAGE(Commands.SEND_MESSAGE, SendMessageCommand::new);

    private String command;
    private Function<EntityManager, ExecutableCommand> executableCommand;

    public static TelegramCommand forCommand(String command) {
        return Stream.of(TelegramCommand.values())
                .filter(value -> value.command.equals(command))
                .findFirst()
                .orElse(TelegramCommand.NOTHING);
    }

    public ExecutableCommand getExecutableCommand(EntityManager entityManager) {
        return executableCommand.apply(entityManager);
    }
}