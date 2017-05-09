package info.openrpg.telegram.commands;

import info.openrpg.constants.Commands;
import lombok.AllArgsConstructor;

import java.util.stream.Stream;

@AllArgsConstructor
public enum TelegramCommand {
    NOTHING(Commands.NO_COMMAND),
    START(Commands.START),
    HELP(Commands.HELP),
    PLAYER_INFO(Commands.PLAYER_INFO),
    PEEK_PLAYER(Commands.PEEK_PLAYER),
    SEND_MESSAGE(Commands.SEND_MESSAGE);

    private String command;

    public static TelegramCommand forCommand(String command) {
        return Stream.of(TelegramCommand.values())
                .filter(value -> value.command.equals(command))
                .findFirst()
                .orElse(TelegramCommand.NOTHING);
    }

}