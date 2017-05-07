package info.openrpg.telegram.command.action;

import info.openrpg.telegram.command.InlineCommands;
import info.openrpg.telegram.command.MessagesEnum;
import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

public class HelpCommand implements ExecutableCommand {

    static final String HELP_MESSAGE = "Все возможные команды";

    @Override
    public List<SendMessage> execute(EntityManager entityManager, InputMessage inputMessage) {
        return Collections.singletonList(MessagesEnum.HELP.sendTo(inputMessage.getChatId()));
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, InputMessage inputMessage) {
        return Collections.emptyList();
    }
}
