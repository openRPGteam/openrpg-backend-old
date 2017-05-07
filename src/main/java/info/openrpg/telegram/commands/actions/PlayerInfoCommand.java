package info.openrpg.telegram.commands.actions;

import com.google.common.base.Joiner;
import info.openrpg.db.player.Player;
import info.openrpg.telegram.commands.InlineCommands;
import info.openrpg.telegram.commands.MessagesEnum;
import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PlayerInfoCommand implements ExecutableCommand {

    private static final String NOT_FOUNT_PLAYER_MESSAGE = "Такого пидора пока нет";
    private static final String PLAYER_NAME_HEADER_MESSAGE = "Пидора зовут: ";
    private static final Joiner JOINER = Joiner.on(" ").skipNulls();

    @Override
    public List<SendMessage> execute(EntityManager entityManager, InputMessage inputMessage) {
        return Optional.of(inputMessage)
                .filter(iM -> iM.hasArguments(1))
                .map(iM -> iM.getArgument(1))
                .map(userName -> getPlayerInfo(entityManager, userName, inputMessage.getChatId()))
                .orElseGet(() -> createPlayerButton(entityManager, 0, inputMessage.getChatId()));
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
                                .setText(JOINER.join(PLAYER_NAME_HEADER_MESSAGE, player.getFirstName(), player.getLastName()))
                )
                .map(sendMessage -> {
                    List<SendMessage> messages = new ArrayList<>();
                    messages.add(sendMessage);
                    messages.add(MessagesEnum.HELP.sendTo(chatId));
                    return messages;
                })
                .orElse(Collections.singletonList(new SendMessage().setChatId(chatId).setText(NOT_FOUNT_PLAYER_MESSAGE)));
    }

    private List<Player> getPlayers(EntityManager entityManager, int offset) {
        return entityManager.createQuery("from Player p ", Player.class)
                .setMaxResults(10)
                .setFirstResult(offset)
                .getResultList();
    }

    private Integer getPlayersNumber(EntityManager entityManager) {
        return entityManager.createQuery("select count(*) from Player", Long.class)
                .getFirstResult();
    }

    private List<SendMessage> createPlayerButton(EntityManager entityManager, int offset, long chatId) {
        int playersNumber = getPlayersNumber(entityManager);
        SendMessage sendMessage = new SendMessage()
                .setText("Список игроков:")
                .setReplyMarkup(InlineCommands.playerInfoInlineCommands(getPlayers(entityManager, offset), offset, playersNumber))
                .setChatId(chatId);
        return Collections.singletonList(sendMessage);
    }
}
