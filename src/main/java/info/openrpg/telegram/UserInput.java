package info.openrpg.telegram;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public class UserInput {
    private String command;
    private String[] arguments;

    public UserInput(String inputString) {
        this.arguments = Optional.of(inputString)
                .map(String::trim)
                .map(s -> s.split(" "))
                .map(arr -> {
                    this.command = arr[0];
                    return arr;
                })
                .filter(arr -> arr.length > 1)
                .map(arr -> Arrays.copyOfRange(arr, 1, arr.length))
                .orElse( null);
    }

    public boolean hasArguments() {
        return arguments != null;
    }
}
