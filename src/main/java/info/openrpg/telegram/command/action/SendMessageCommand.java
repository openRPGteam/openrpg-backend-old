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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SendMessageCommand implements ExecutableCommand {

    private static final Joiner JOINER = Joiner.on(" ").skipNulls();
    private static final String UNKNOWN_PLAYER_MESSAGE = "Ты попытался потыкать палкой несуществующего пидора.";
    private static final String PLAYER_PEEKED_MESSAGE = "Тебе передал сообщение";
    private static final String WRONG_FORMAT_MESSAGE = "Неправильный формат команды\n" +
            "Пример:\n" +
            "/send_message DarkCasual Привет";

    @Override
    public List<SendMessage> execute(EntityManager entityManager, Update update, UserInput userInput) {
        return Optional.of(userInput)
                .filter(ui -> ui.hasArguments(2))
                .map(ui -> entityManager.createQuery("from Player p where p.userName = :userName", Player.class)
                        .setParameter("userName", userInput.getArgument(1))
                        .getResultList()
                        .stream()
                        .findFirst()
                        .map(player -> new SendMessage()
                                .setChatId(update.getMessage().getChatId())
                                .setText(JOINER.join(
                                        PLAYER_PEEKED_MESSAGE,
                                        "@".concat(userInput.getArgument(1)).concat(":"),
                                        JOINER.join(
                                                IntStream.rangeClosed(2, ui.size())
                                                        .mapToObj(ui::getArgument)
                                                        .collect(Collectors.toList()))
                                        )
                                )
                        )
                        .map(Collections::singletonList)
                        .orElse(Collections.singletonList(new SendMessage()
                                        .setChatId(update.getMessage().getChatId())
                                        .setText(UNKNOWN_PLAYER_MESSAGE)
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
