package info.openrpg.telegram.input;

import info.openrpg.telegram.command.CommandChooser;
import info.openrpg.telegram.command.TelegramCommand;
import lombok.Getter;
import org.telegram.telegrambots.api.objects.User;

import java.util.Arrays;
import java.util.Optional;

@Getter
public class InputMessage {
    private final String text;
    private final Long chatId;
    private final User from;
    private final TelegramCommand command;
    private final String[] arguments;
    private final boolean isCallback;

    public InputMessage(String inputString, Long chatId, User from, CommandChooser commandChooser, boolean isCallback) {
        this.isCallback = isCallback;
        this.chatId = chatId;
        this.from = from;
        this.text = inputString;
        this.command = Optional.of(inputString)
                .map(String::trim)
                .map(s -> s.split(" "))
                .filter(arr -> arr.length > 0)
                .map(arr -> commandChooser.chooseCommand(arr[0]))
                .orElse(TelegramCommand.NOTHING);
        this.arguments = Optional.of(inputString)
                .map(String::trim)
                .map(s -> s.split(" "))
                .filter(arr -> arr.length > 1)
                .map(arr -> Arrays.copyOfRange(arr, 1, arr.length))
                .orElse(null);
    }

    public boolean hasArguments() {
        return arguments != null;
    }

    public boolean hasArguments(int numberOfArguments) {
        return hasArguments() && arguments.length >= numberOfArguments;
    }

    public String getArgument(int index) {
        return arguments[index - 1];
    }

    public int size() {
        return arguments != null ? arguments.length : 0;
    }
}
