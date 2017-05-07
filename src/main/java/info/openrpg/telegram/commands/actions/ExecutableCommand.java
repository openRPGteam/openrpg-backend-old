package info.openrpg.telegram.commands.actions;

import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import javax.persistence.EntityManager;
import java.util.List;

public interface ExecutableCommand {
    List<SendMessage> execute(EntityManager entityManager, InputMessage inputMessage);
    List<SendMessage> handleCrash(RuntimeException e, InputMessage inputMessage);
}
