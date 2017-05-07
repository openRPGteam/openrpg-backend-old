package info.openrpg.telegram.commands;

import info.openrpg.telegram.commands.actions.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TelegramCommand {
    NOTHING(new DoNothingComand()),
    START(new StartCommand()),
    HELP(new HelpCommand()),
    PLAYER_INFO(new PlayerInfoCommand()),
    PEEK_PLAYER(new PeekPlayerCommand()),
    SEND_MESSAGE(new SendMessageCommand());

    private ExecutableCommand executableCommand;
}