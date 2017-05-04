package info.openrpg.telegram.command.action;

import com.google.common.base.Joiner;
import info.openrpg.db.player.Player;
import info.openrpg.telegram.UserInput;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PlayerInfoCommand implements ExecutableCommand {

    private static final String WRONG_FORMAT_MESSAGE =
            "Неправильный формат команды.\n" +
                    "Пример:\n" +
                    "/player_info DarkCasual";
    private static final String NOT_FOUNT_PLAYER_MESSAGE = "Такого пидора пока нет";
    private static final String PLAYER_NAME_HEADER_MESSAGE = "Пидора зовут: ";
    private static final Joiner JOINER = Joiner.on(" ").skipNulls();

    @Override
    public List<SendMessage> execute(EntityManager entityManager, Update update, UserInput userInput) {
        return Optional.of(userInput)
                .filter(UserInput::hasArguments)
                .map(ui -> entityManager.createQuery("from Player p where p.userName = :id", Player.class)
                        .setParameter("id", userInput.getArguments()[0])
                        .getResultList()
                        .stream()
                        .findFirst()
                        .map(player -> new SendMessage()
                                .setChatId(update.getMessage().getChatId())
                                .setText(JOINER.join(PLAYER_NAME_HEADER_MESSAGE, player.getFirstName(), player.getLastName()))
                        )
                        .map(Collections::singletonList)
                        .orElse(Collections.singletonList(new SendMessage()
                                .setChatId(update.getMessage().getChatId())
                                .setText(NOT_FOUNT_PLAYER_MESSAGE)
                                )
                        )
                )
                .orElse(Collections.singletonList(
                        new SendMessage()
                                .setChatId(update.getMessage().getChatId())
                                .setText(WRONG_FORMAT_MESSAGE)
                        )
                );
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, Update update) {
        return Collections.emptyList();
    }
}
