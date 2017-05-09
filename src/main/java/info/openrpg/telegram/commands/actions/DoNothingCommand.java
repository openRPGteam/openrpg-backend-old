package info.openrpg.telegram.commands.actions;

import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

public class DoNothingCommand extends ExecutableCommand {

    public DoNothingCommand(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public List<SendMessage> execute(InputMessage inputMessage) {
        return Collections.emptyList();
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, InputMessage inputMessage) {
        return Collections.emptyList();
    }
}
