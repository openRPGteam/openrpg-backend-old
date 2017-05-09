package info.openrpg.telegram.commands.actions;

import info.openrpg.telegram.commands.TelegramCommand;
import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.util.List;

public interface CommandExecutor {

    List<SendMessage> execute(InputMessage inputMessage);
    boolean isCommandSupported(TelegramCommand command);
    List<SendMessage> handleCrash(RuntimeException e, InputMessage inputMessage);
}
