package info.openrpg.telegram.command.action;

import com.google.common.base.Joiner;
import info.openrpg.db.player.Chat;
import info.openrpg.db.player.Player;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PeekPlayer implements ExecutableCommand {
    private static final Joiner JOINER = Joiner.on(" ").skipNulls();
    public static final String UNKNOWN_PLAYER_MESSAGE = "Ты попытался потыкать палкой несуществующего пидора.";
    public static final String PLAYER_PEEKED_MESSAGE = "Тебя потыкал палкой";

    @Override
    public List<SendMessage> execute(EntityManager entityManager, Update update) {
        return entityManager.createQuery("select c from Chat c join Player p on p.id = c.player where p.userName = :userName", Chat.class)
                .setParameter("userName", update.getMessage().getText().split(" ")[1])
                .getResultList()
                .stream()
                .findFirst()
                .map(chat -> new SendMessage()
                        .setChatId(chat.getId())
                        .setText(JOINER.join("Тебя потыкал палкой", "@".concat(update.getMessage().getFrom().getUserName())))
                        .enableHtml(true)
                )
                .map(Collections::singletonList)
                .orElse(entityManager.createQuery("from Player p where p.userName = :id", Player.class)
                                .setParameter("id", update.getMessage().getText().split(" ")[1])
                                .getResultList()
                                .stream()
                                .findFirst()
                                .map(player -> new SendMessage()
                                        .setChatId(String.valueOf(player.getId()))
                                        .setText(JOINER.join(PLAYER_PEEKED_MESSAGE, "@".concat(update.getMessage().getFrom().getUserName())))
                                        .enableHtml(true)
                                )
                                .map(Collections::singletonList)
                                .orElseGet(() -> {
                                    SendMessage sendMessage = new SendMessage()
                                            .setChatId(update.getMessage().getChatId())
                                            .setText(UNKNOWN_PLAYER_MESSAGE);
                                    return Collections.singletonList(sendMessage);
                                })
                );
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, Update update) {
        return Collections.emptyList();
    }
}
