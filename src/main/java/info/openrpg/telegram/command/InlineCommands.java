package info.openrpg.telegram.command;

import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InlineCommands {

    public InlineKeyboardMarkup helpInlineCommands() {
        List<List<InlineKeyboardButton>> keys = new ArrayList<>();
        keys.add(createInlineKeyboardButtonRow("Начать игру", "/start"));
        keys.add(createInlineKeyboardButtonRow("Помощь", "/help"));
        keys.add(createInlineKeyboardButtonRow("Просмотреть информацию", "/player_info"));
        keys.add(createInlineKeyboardButtonRow("Тыркнуть палкой", "/peek_player"));
        return new InlineKeyboardMarkup().setKeyboard(keys);
    }

    public List<InlineKeyboardButton> createInlineKeyboardButtonRow(String text, String callback) {
        return Collections.singletonList(createInlineKeyboardButton(text, callback));
    }

    public InlineKeyboardButton createInlineKeyboardButton(String text, String callback) {
        return new InlineKeyboardButton().setText(text).setCallbackData(callback);
    }
}
