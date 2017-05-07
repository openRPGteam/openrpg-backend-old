package info.openrpg.telegram;

import info.openrpg.db.player.Chat;
import info.openrpg.db.player.Player;
import info.openrpg.telegram.command.CommandChooser;
import info.openrpg.telegram.command.InlineCommands;
import info.openrpg.telegram.command.TelegramCommand;
import info.openrpg.telegram.command.action.ExecutableCommand;
import info.openrpg.telegram.input.InputMessage;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
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
    private final CommandChooser commandChooser;

    private Credentials credentials;
    private SessionFactory sessionFactory;
    private InlineCommands inlineCommands;

    public OpenRpgBot(Credentials credentials, Properties properties) {
        this.credentials = credentials;
        this.commandChooser = new CommandChooser();
        this.sessionFactory = new Configuration()
                .addPackage("db.player")
                .addProperties(properties)
                .addAnnotatedClass(Player.class)
                .addAnnotatedClass(Chat.class)
                .buildSessionFactory();
        inlineCommands = new InlineCommands();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Optional.of(update)
                .map(this::parseInputMessage)
                .flatMap(inputMessage -> inputMessage)
                .map(this::logInputMessage)
                .ifPresent(this::parseCommand);
    }

    private InputMessage logInputMessage(InputMessage inputMessage) {
        logger.info(inputMessage.getChatId().toString());
        logger.info(inputMessage.getFrom().toString());
        logger.info(inputMessage.getText());
        return inputMessage;
    }

    @Override
    public String getBotUsername() {
        return credentials.getBotName();
    }

    @Override
    public String getBotToken() {
        return credentials.getToken();
    }

    private Optional<InputMessage> parseInputMessage(Update update) {
        return Optional.of(update)
                .map(Update::getMessage)
                .map(message -> new InputMessage(
                        message.getText(),
                        message.getChatId(),
                        message.getFrom(),
                        commandChooser,
                        false)
                )
                .map(Optional::of)
                .orElseGet(() ->
                        Optional.of(update)
                                .map(Update::getCallbackQuery)
                                .map(callbackQuery -> {
                                    answerCallbackText(callbackQuery.getId());
                                    return callbackQuery;
                                })
                                .map(callbackQuery ->
                                        new InputMessage(
                                                callbackQuery.getData(),
                                                Long.valueOf(callbackQuery.getFrom().getId()),
                                                callbackQuery.getFrom(),
                                                commandChooser,
                                                true)
                                )
                );
    }

    private void sendText(SendMessage sendMessage) {
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void answerCallbackText(String callbackQueryId) {
        try {
            answerCallbackQuery(new AnswerCallbackQuery().setCallbackQueryId(callbackQueryId).setShowAlert(false));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void parseCommand(InputMessage inputMessage) {
        Optional.of(inputMessage)
                .map(InputMessage::getCommand)
                .filter(command -> command != TelegramCommand.NOTHING)
                .map(TelegramCommand::getExecutableCommand)
                .ifPresent(executableCommand -> {
                    executeCommand(executableCommand, inputMessage);
                });
    }

    private void executeCommand(ExecutableCommand executableCommand, InputMessage inputMessage) {
        EntityManager entityManager = sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            List<SendMessage> sendMessageList = executableCommand.execute(entityManager, inputMessage);
            entityManager.getTransaction().commit();
            sendMessageList.forEach(this::sendText);
        } catch (RuntimeException e) {
            handleCrash(e, entityManager, executableCommand, inputMessage);
        }
        entityManager.close();
    }

    private void handleCrash(
            RuntimeException e,
            EntityManager entityManager,
            ExecutableCommand executableCommand,
            InputMessage inputMessage
    ) {
        entityManager.getTransaction().rollback();
        Optional.of(executableCommand.handleCrash(e, inputMessage))
                .filter(sendMessages -> !sendMessages.isEmpty())
                .orElseGet(() -> {
                    logger.warning(e.getClass().getName());
                    e.printStackTrace();
                    return Collections.singletonList(
                            new SendMessage()
                                    .setText("Извини, что-то пошло не так.\nРазработчик получает пизды.")
                                    .setChatId(inputMessage.getChatId())
                    );
                })
                .forEach(this::sendText);
    }
}
