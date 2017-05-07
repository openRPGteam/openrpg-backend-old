package info.openrpg.telegram.command.action;

import info.openrpg.telegram.command.InlineCommands;
import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

public class HelpCommand implements ExecutableCommand {

    private static final String HELP_MESSAGE = "Все возможные команды";
    private final InlineCommands inlineCommands;

    public HelpCommand() {
        this.inlineCommands = new InlineCommands();
    }

    @Override
    public List<SendMessage> execute(EntityManager entityManager, InputMessage inputMessage) {
        return Collections.singletonList(
                new SendMessage()
                        .setChatId(inputMessage.getChatId())
                        .setText(HELP_MESSAGE)
                        .setReplyMarkup(inlineCommands.helpInlineCommands())
                        .enableHtml(true));
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, InputMessage inputMessage) {
        return Collections.emptyList();
    }
}
