package info.openrpg.telegram.command.action;

import com.google.common.base.Joiner;
import info.openrpg.db.player.Player;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

public class PlayerInfoCommand implements ExecutableCommand {

    private static final String WRONG_FORMAT_MESSAGE =
            "Неправильный формат команды.\n" +
                    "Пример:\n" +
                    "/player_info DarkCasual";
    private static final String NOT_FOUNT_PLAYER_MESSAGE = "Такого пидора пока нет";
    private static final String PLAYER_NAME_HEADER_MESSAGE = "Пидора зовут: ";
    private static final Joiner JOINER = Joiner.on(" ").skipNulls();

    @Override
    public List<SendMessage> execute(EntityManager entityManager, Update update) {
        return entityManager.createQuery("from Player p where p.userName = :id", Player.class)
                .setParameter("id", update.getMessage().getText().split(" ")[1])
                .getResultList()
                .stream()
                .findFirst()
                .map(player -> new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText(JOINER.join(PLAYER_NAME_HEADER_MESSAGE, player.getFirstName(), player.getLastName()))
                        .enableHtml(true)
                )
                .map(Collections::singletonList)
                .orElse(Collections.singletonList(
                        new SendMessage()
                                .setChatId(update.getMessage().getChatId())
                                .setText(NOT_FOUNT_PLAYER_MESSAGE)
                                .enableHtml(true))
                );
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, Update update) {
        if (e instanceof ArrayIndexOutOfBoundsException) {
            return Collections.singletonList(
                    new SendMessage()
                            .setChatId(update.getMessage().getChatId())
                            .setText(WRONG_FORMAT_MESSAGE)
            );
        }
        return Collections.emptyList();
    }
}
