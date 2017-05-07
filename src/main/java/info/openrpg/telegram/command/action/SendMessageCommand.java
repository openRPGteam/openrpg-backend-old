package info.openrpg.telegram.command.action;

import com.google.common.base.Joiner;
import info.openrpg.db.player.Player;
import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

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
    public List<SendMessage> execute(EntityManager entityManager, InputMessage inputMessage) {
        return Optional.of(inputMessage)
                .filter(iM -> iM.hasArguments(2))
                .map(iM -> entityManager.createQuery("from Player p where p.userName = :userName", Player.class)
                        .setParameter("userName", iM.getArgument(1))
                        .getResultList()
                        .stream()
                        .findFirst()
                        .map(player -> new SendMessage()
                                .setChatId(new Long(player.getId()))
                                .setText(JOINER.join(
                                        PLAYER_PEEKED_MESSAGE,
                                        "@".concat(inputMessage.getFrom().getUserName()).concat(":"),
                                        JOINER.join(
                                                IntStream.rangeClosed(2, iM.size())
                                                        .mapToObj(iM::getArgument)
                                                        .collect(Collectors.toList()))
                                        )
                                )
                        )
                        .map(Collections::singletonList)
                        .orElse(Collections.singletonList(new SendMessage()
                                        .setChatId(inputMessage.getChatId())
                                        .setText(UNKNOWN_PLAYER_MESSAGE)
                                )
                        )
                )
                .orElse(Collections.singletonList(
                        new SendMessage()
                                .setChatId(inputMessage.getChatId())
                                .setText(WRONG_FORMAT_MESSAGE)
                        )
                );
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, InputMessage inputMessage) {
        return Collections.emptyList();
    }
}
