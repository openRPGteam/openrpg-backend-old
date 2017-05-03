package info.openrpg.telegram.command.action;

import info.openrpg.db.player.Chat;
import info.openrpg.db.player.Player;
import info.openrpg.telegram.UserInput;
import org.hibernate.exception.ConstraintViolationException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.Collections;
import java.util.List;

public class StartCommand implements ExecutableCommand {

    public static final String ALREADY_REGISTERED_MESSAGE = "Ты уже зарегистрирован";
    public static final String FIRST_MESSAGE = "Спасибо за регистрацию";

    @Override
    public List<SendMessage> execute(EntityManager entityManager, Update update, UserInput userInput) {
        User user = update.getMessage().getFrom();
        Player player = Player.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .build();
        Chat chat = Chat.builder()
                .id(update.getMessage().getChatId())
                .player(player)
                .build();

        entityManager.persist(player);
        entityManager.persist(chat);

        return Collections.singletonList(
                new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText(FIRST_MESSAGE)
        );
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, Update update) {
        if (e instanceof PersistenceException) {
            if (e.getCause() instanceof ConstraintViolationException) {
                return Collections.singletonList(
                        new SendMessage()
                                .setChatId(update.getMessage().getChatId())
                                .setText(ALREADY_REGISTERED_MESSAGE)
                );
            }
        }
        return Collections.emptyList();
    }
}
