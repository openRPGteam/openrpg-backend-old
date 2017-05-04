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

public class PeekPlayerCommand implements ExecutableCommand {
    private static final Joiner JOINER = Joiner.on(" ").skipNulls();
    private static final String UNKNOWN_PLAYER_MESSAGE = "Ты попытался потыкать палкой несуществующего пидора.";
    private static final String PLAYER_PEEKED_MESSAGE = "Тебя потыкал палкой";
    private static final String WRONG_ARGUMENTS_NUMBER_MESSAGE = "Неправильный формат команды\n" +
            "Пример:\n" +
            "/peek_player DarkCasual";

    @Override
    public List<SendMessage> execute(EntityManager entityManager, Update update, UserInput userInput) {
        return Optional.of(userInput)
                .filter(ui -> ui.hasArguments(1))
                .map(ui -> ui.getArgument(1))
                .map(userName -> getPlayerByUsername(entityManager, update, userName))
                .map(Collections::singletonList)
                .orElse(Collections.singletonList(
                        new SendMessage()
                                .setChatId(update.getMessage().getChatId())
                                .setText(WRONG_ARGUMENTS_NUMBER_MESSAGE)
                        )
                );
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, Update update) {
        return Collections.emptyList();
    }

    private SendMessage getPlayerByUsername(EntityManager entityManager, Update update, String userName) {
        return entityManager.createQuery("from Player p where p.userName = :userName", Player.class)
                .setParameter("userName", userName)
                .getResultList()
                .stream()
                .findFirst()
                .map(player -> new SendMessage()
                        .setChatId(String.valueOf(player.getId()))
                        .setText(JOINER.join(PLAYER_PEEKED_MESSAGE, "@".concat(update.getMessage().getFrom().getUserName())))
                )
                .orElse(new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText(UNKNOWN_PLAYER_MESSAGE)
                );
    }
}
