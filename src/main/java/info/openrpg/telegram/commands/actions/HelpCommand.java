package info.openrpg.telegram.commands.actions;

import info.openrpg.telegram.commands.MessagesEnum;
import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

public class HelpCommand extends ExecutableCommand {

    public HelpCommand(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public List<SendMessage> execute(InputMessage inputMessage) {
        return Collections.singletonList(MessagesEnum.HELP.sendTo(inputMessage.getChatId()));
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, InputMessage inputMessage) {
        return Collections.emptyList();
    }
}
