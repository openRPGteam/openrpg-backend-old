package info.openrpg.telegram.command.action;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

public class HelpCommand implements ExecutableCommand {

    @Override
    public List<SendMessage> execute(EntityManager entityManager, Update update) {
        return Collections.singletonList(
                new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText("Ты пидор").enableHtml(true));
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, Update update) {
        return Collections.emptyList();
    }
}
