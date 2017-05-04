package info.openrpg.telegram;

import info.openrpg.telegram.command.CommandChooser;
import info.openrpg.telegram.command.TelegramCommand;

import java.util.Arrays;
import java.util.Optional;

public class UserInput {
    private TelegramCommand command;
    private String[] arguments;

    public UserInput(String inputString, CommandChooser commandChooser) {
        this.arguments = Optional.of(inputString)
                .map(String::trim)
                .map(s -> s.split(" "))
                .map(arr -> {
                    this.command = commandChooser.chooseCommand(arr[0]);
                    return arr;
                })
                .filter(arr -> arr.length > 1)
                .map(arr -> Arrays.copyOfRange(arr, 1, arr.length))
                .orElse( null);
    }

    public TelegramCommand getCommand() {
        return command;
    }

    public boolean hasArguments() {
        return arguments != null;
    }

    public boolean hasArguments(int numberOfArguments) {
        return arguments.length >= numberOfArguments;
    }

    public String getArgument(int index) {
        return arguments[index-1];
    }

    public int size() {
        return arguments != null ? arguments.length : 0;
    }

}
