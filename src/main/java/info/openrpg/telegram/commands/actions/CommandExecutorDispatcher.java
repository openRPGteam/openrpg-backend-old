package info.openrpg.telegram.commands.actions;

import info.openrpg.database.repositories.MessageRepository;
import info.openrpg.database.repositories.PlayerRepository;
import info.openrpg.database.repositories.PostgresPlayerRepository;
import info.openrpg.database.repositories.PostrgresMessageRepository;
import info.openrpg.telegram.commands.TelegramCommand;
import info.openrpg.telegram.input.InputMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandExecutorDispatcher {

    private final List<CommandExecutor> commandExecutors = new ArrayList<>();

    public CommandExecutorDispatcher(EntityManager entityManager) {
        initExecutors(entityManager);
    }

    private void initExecutors(EntityManager entityManager) {
        PlayerRepository playerRepository = new PostgresPlayerRepository(entityManager);
        MessageRepository messageRepository = new PostrgresMessageRepository(entityManager);
        commandExecutors.add(new DoNothingCommand());
        commandExecutors.add(new HelpCommand());
        commandExecutors.add(new PeekPlayerCommand(playerRepository));
        commandExecutors.add(new PlayerInfoCommand(playerRepository));
        commandExecutors.add(new StartCommand(playerRepository));
        commandExecutors.add(new SendMessageCommand(playerRepository, messageRepository));
    }

    public List<SendMessage> execute(InputMessage message) {
        return getAcceptableExecutor(message.getCommand())
                .orElseThrow(RuntimeException::new)
                .execute(message);
    }

    public List<SendMessage> handleCrash(RuntimeException e, InputMessage message) {
        return getAcceptableExecutor(message.getCommand())
                .orElseThrow(RuntimeException::new)
                .handleCrash(e, message);

    }

    private Optional<CommandExecutor> getAcceptableExecutor(TelegramCommand command) {
        return commandExecutors.stream()
                .filter(executor -> executor.isCommandSupported(command))
                .findAny();
    }
}
