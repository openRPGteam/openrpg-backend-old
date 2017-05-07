package info.openrpg.telegram.command.action;

import com.google.common.base.Joiner;
import info.openrpg.db.player.Player;
import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

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
    public List<SendMessage> execute(EntityManager entityManager, InputMessage inputMessage) {
        return Optional.of(inputMessage)
                .filter(iM -> iM.hasArguments(1))
                .map(iM -> iM.getArgument(1))
                .map(userName -> getPlayerInfo(entityManager, userName, inputMessage.getChatId()))
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

    private List<SendMessage> getPlayerInfo(EntityManager entityManager, String userName, Long chatId) {
        return entityManager.createQuery("from Player p where p.userName = :userName", Player.class)
                .setParameter("userName", userName)
                .getResultList()
                .stream()
                .findFirst()
                .map(player ->
                        new SendMessage()
                                .setChatId(chatId)
                                .setText(
                                        JOINER.join(PLAYER_NAME_HEADER_MESSAGE, player.getFirstName(), player.getLastName())
                                )
                )
                .map(Collections::singletonList)
                .orElse(Collections.singletonList(new SendMessage().setChatId(chatId).setText(NOT_FOUNT_PLAYER_MESSAGE)));
    }
}
