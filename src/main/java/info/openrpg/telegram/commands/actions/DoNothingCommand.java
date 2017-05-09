package info.openrpg.telegram.commands.actions;

import info.openrpg.telegram.commands.TelegramCommand;
import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.util.Collections;
import java.util.List;

public class DoNothingCommand implements CommandExecutor {


    @Override
    public List<SendMessage> execute(InputMessage inputMessage) {
        return Collections.emptyList();
    }

    @Override
    public boolean isCommandSupported(TelegramCommand command) {
        return command == TelegramCommand.NOTHING;
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, InputMessage inputMessage) {
        return Collections.emptyList();
    }
}
