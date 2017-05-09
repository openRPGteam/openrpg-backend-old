package info.openrpg.telegram.commands.actions;

import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import javax.persistence.EntityManager;
import java.util.List;

public abstract class ExecutableCommand {

    ExecutableCommand(EntityManager entityManager) {
    }

    public abstract List<SendMessage> execute(InputMessage inputMessage);
    public abstract List<SendMessage> handleCrash(RuntimeException e, InputMessage inputMessage);
}
