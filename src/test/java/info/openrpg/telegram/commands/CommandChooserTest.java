package info.openrpg.telegram.commands;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CommandChooserTest {

    @Test
    public void testVoidCommand() throws Exception {
        assertEquals(TelegramCommand.NOTHING, TelegramCommand.forCommand("/"));
    }

    @Test
    public void testVoidCommandWithoutSLash() throws Exception {
        assertEquals(TelegramCommand.NOTHING, TelegramCommand.forCommand(""));
    }

    @Test
    public void testHelp() throws Exception {
        assertEquals(TelegramCommand.HELP, TelegramCommand.forCommand("/help"));
    }

    @Test
    public void testPlayerInfo() throws Exception {
        assertEquals(TelegramCommand.NOTHING, TelegramCommand.forCommand("/player_info 123123"));
    }

    @Test
    public void testPlayerInfoWithoutArg() throws Exception {
        assertEquals(TelegramCommand.PLAYER_INFO, TelegramCommand.forCommand("/player_info"));
    }

    @Test
    public void testStart() throws Exception {
        assertEquals(TelegramCommand.START, TelegramCommand.forCommand("/start"));
    }
}