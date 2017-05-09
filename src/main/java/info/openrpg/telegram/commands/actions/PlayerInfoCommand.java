package info.openrpg.telegram.commands.actions;

import com.google.common.base.Joiner;
import info.openrpg.constants.Commands;
import info.openrpg.database.models.Player;
import info.openrpg.database.repositories.PlayerRepository;
import info.openrpg.database.repositories.PostgresPlayerRepository;
import info.openrpg.telegram.commands.InlineCommands;
import info.openrpg.telegram.commands.MessagesEnum;
import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PlayerInfoCommand extends ExecutableCommand {

    private static final String NOT_FOUNT_PLAYER_MESSAGE = "Такого пидора пока нет";
    private static final String PLAYER_NAME_HEADER_MESSAGE = "Пидора зовут: ";
    private static final Joiner JOINER = Joiner.on(" ").skipNulls();

    private final PlayerRepository playerRepository;

    public PlayerInfoCommand(EntityManager entityManager) {
        super(entityManager);
        this.playerRepository = new PostgresPlayerRepository(entityManager);
    }

    @Override
    public List<SendMessage> execute(InputMessage inputMessage) {
        return Optional.of(inputMessage)
                .filter(iM -> iM.hasArguments(1))
                .map(iM -> iM.getArgument(1))
                .map(userName -> getPlayerInfo(userName, inputMessage.getChatId()))
                .orElseGet(() -> playersButtonList(0, inputMessage.getChatId()));
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, InputMessage inputMessage) {
        return Collections.emptyList();
    }

    private List<SendMessage> getPlayerInfo(String userName, Long chatId) {
        return playerRepository.findPlayerByUsername(userName)
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

    private List<SendMessage> playersButtonList(int offset, long chatId) {
        int playersNumber = playerRepository.selectPlayersNumber();
        List<Player> players = playerRepository.selectPlayerWithOffset(offset, 10);
        SendMessage sendMessage = new SendMessage()
                .setText("Список игроков:")
                .setReplyMarkup(InlineCommands.playerList(Commands.PLAYER_INFO, players, offset, playersNumber))
                .setChatId(chatId);
        return Collections.singletonList(sendMessage);
    }
}
