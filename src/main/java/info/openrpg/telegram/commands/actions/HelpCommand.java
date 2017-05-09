package info.openrpg.telegram.commands.actions;

import info.openrpg.telegram.commands.MessagesEnum;
import info.openrpg.telegram.commands.TelegramCommand;
import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.util.Collections;
import java.util.List;

public class HelpCommand implements CommandExecutor {

    @Override
    public List<SendMessage> execute(InputMessage inputMessage) {
        return Collections.singletonList(MessagesEnum.HELP.sendTo(inputMessage.getChatId()));
    }

    @Override
    public boolean isCommandSupported(TelegramCommand command) {
        return command == TelegramCommand.HELP;
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, InputMessage inputMessage) {
        return Collections.emptyList();
    }
}
