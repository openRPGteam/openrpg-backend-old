package info.openrpg.telegram;

import info.openrpg.db.player.Chat;
import info.openrpg.db.player.Player;
import info.openrpg.telegram.command.CommandChooser;
import info.openrpg.telegram.command.TelegramCommand;
import info.openrpg.telegram.command.action.ExecutableCommand;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

public class OpenRpgBot extends TelegramLongPollingBot {
    private static final Logger logger = Logger.getLogger("bot");

    private Credentials credentials;
    private SessionFactory sessionFactory;
    private CommandChooser commandChooser;

    public OpenRpgBot(Credentials credentials, Properties properties) {
        this.credentials = credentials;
        this.sessionFactory = new Configuration()
                .addPackage("db.player")
                .addProperties(properties)
                .addAnnotatedClass(Player.class)
                .addAnnotatedClass(Chat.class)
                .buildSessionFactory();
        commandChooser = new CommandChooser();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Optional.of(update)
                .map(Update::getMessage)
                .map(Message::getText)
                .map(text -> {
                    logger.info(text);
                    return text;
                })
                .map(text -> new UserInput(text, commandChooser))
                .ifPresent(userInput -> validateInput(update, userInput));
    }

    @Override
    public String getBotUsername() {
        return credentials.getBotName();
    }

    @Override
    public String getBotToken() {
        return credentials.getToken();
    }

    private void sendText(SendMessage sendMessage) {
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void validateInput(final Update update, final UserInput userInput) {
        Optional.of(userInput)
                .map(UserInput::getCommand)
                .filter(command -> command != TelegramCommand.NOTHING)
                .map(TelegramCommand::getExecutableCommand)
                .ifPresent(executableCommand -> executeCommand(executableCommand, update, userInput));
    }

    private void executeCommand(ExecutableCommand executableCommand, Update update, UserInput userInput) {
        EntityManager entityManager = sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            List<SendMessage> sendMessageList = executableCommand.execute(entityManager, update, userInput);
            entityManager.getTransaction().commit();
            sendMessageList.forEach(this::sendText);
        } catch (RuntimeException e) {
            handleCrash(e, entityManager, executableCommand, update);
        }
        entityManager.close();
    }

    private void handleCrash(
            RuntimeException e,
            EntityManager entityManager,
            ExecutableCommand executableCommand,
            Update update
    ) {
        entityManager.getTransaction().rollback();
        Optional.of(executableCommand.handleCrash(e, update))
                .filter(sendMessages -> !sendMessages.isEmpty())
                .orElseGet(() -> {
                    logger.warning(e.getClass().getName());
                    e.printStackTrace();
                    return Collections.singletonList(
                            new SendMessage()
                                    .setText("Извини, что-то пошло не так.\nРазработчик получает пизды.")
                                    .setChatId(update.getMessage().getChatId())
                    );
                })
                .forEach(this::sendText);
    }
}
