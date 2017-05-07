package info.openrpg.telegram.command;

import org.telegram.telegrambots.api.methods.send.SendMessage;

public enum MessagesEnum {
    HELP(new SendMessage().setText("Все возможные команды").setReplyMarkup(InlineCommands.helpInlineCommands()));
    private final SendMessage sendMessage;

    MessagesEnum(SendMessage sendMessage) {
        this.sendMessage = sendMessage;
    }

    public SendMessage sendTo(Long chatId) {
        return sendMessage.setChatId(chatId);
    }
}
