package info.openrpg.telegram.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandChooser {
    private Map<String, TelegramCommand> commandMap;

    public CommandChooser() {
        this.commandMap = new HashMap<>();
        commandMap.put("/help", TelegramCommand.HELP);
        commandMap.put("/player_info", TelegramCommand.PLAYER_INFO);
        commandMap.put("/start", TelegramCommand.START);
        commandMap.put("/peek_player", TelegramCommand.PEEK_PLAYER);
    }

    public TelegramCommand chooseCommand(String rawText) {
        return Optional.of(rawText)
                .filter(text -> text.startsWith("/"))
                .map(text -> Optional.ofNullable(commandMap.get(text))
                        .orElse(TelegramCommand.VOID))
                .orElse(TelegramCommand.VOID);
    }
}
