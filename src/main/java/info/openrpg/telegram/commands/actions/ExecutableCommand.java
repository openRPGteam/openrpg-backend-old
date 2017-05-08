package info.openrpg.telegram.commands.actions;

import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.util.List;

public interface ExecutableCommand {
    List<SendMessage> execute(InputMessage inputMessage);
    List<SendMessage> handleCrash(RuntimeException e, InputMessage inputMessage);
}
