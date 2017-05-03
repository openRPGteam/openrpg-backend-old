package info.openrpg.telegram.command.action;

import info.openrpg.telegram.UserInput;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

public class HelpCommand implements ExecutableCommand {

    private static final String HELP_MESSAGE = "Ты пидор";

    @Override
    public List<SendMessage> execute(EntityManager entityManager, Update update, UserInput userInput) {
        return Collections.singletonList(
                new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText(HELP_MESSAGE).enableHtml(true));
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, Update update) {
        return Collections.emptyList();
    }
}
