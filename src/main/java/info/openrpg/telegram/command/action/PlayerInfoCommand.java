package info.openrpg.telegram.command.action;

import info.openrpg.db.player.Player;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

public class PlayerInfoCommand implements ExecutableCommand {
    @Override
    public List<SendMessage> execute(EntityManager entityManager, Update update) {
        return entityManager.createQuery("from Player p where p.userName = :id", Player.class)
                .setParameter("id", update.getMessage().getText().split(" ")[1])
                .getResultList()
                .stream()
                .findFirst()
                .map(player -> new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText("Пидора зовут: " + player.getFirstName() + " " + player.getLastName())
                        .enableHtml(true)
                )
                .map(Collections::singletonList)
                .orElse(Collections.singletonList(
                        new SendMessage()
                                .setChatId(update.getMessage().getChatId())
                                .setText("Такого пидора пока нет")
                                .enableHtml(true))
                );
    }

    @Override
    public List<SendMessage> handleCrash(RuntimeException e, Update update) {
        return Collections.emptyList();
    }
}
