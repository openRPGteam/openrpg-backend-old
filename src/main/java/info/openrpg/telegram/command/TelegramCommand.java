package info.openrpg.telegram.command;

import info.openrpg.telegram.command.action.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TelegramCommand {
    VOID(new DoNothingComand()),
    START(new StartCommand()),
    HELP(new HelpCommand()),
    PLAYER_INFO(new PlayerInfoCommand()),
    PEEK_PLAYER(new PeekPlayerCommand()),
    SEND_MESSAGE(new SendMessageCommand());

    private ExecutableCommand executableCommand;
}