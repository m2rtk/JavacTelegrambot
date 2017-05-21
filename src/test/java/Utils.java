import bot.JavaBot;
import dao.BotDAO;
import dao.InMemoryBotDAO;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Utils {

    private Utils() {} // prevent instantiation

    static BotDAO changeDAO(JavaBot bot) throws Exception {
        BotDAO dao = new InMemoryBotDAO();

        Field field = JavaBot.class.getDeclaredField("dao");
        field.setAccessible(true);
        field.set(bot, dao);

        return dao;
    }

    static void setObjectField(Object object, String fieldName, Object newValue) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL); // remove final
        field.setAccessible(true);
        field.set(object, newValue);
    }

    static String readSource(String name) throws Exception {
        return String.join("\n", Files.readAllLines(Paths.get(Utils.class.getClassLoader().getResource("src/" + name + ".java").toURI())).stream().collect(Collectors.toList()));
    }

    static byte[] readOut(String name) throws Exception {
        return Files.readAllBytes(Paths.get(Utils.class.getClassLoader().getResource("out/" + name + ".class").toURI()));
    }

    static Update createMockUpdateWithTextContent(String content, Long userId, Long chatId) {
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId.intValue());
        Message mockMessage = mock(Message.class);
        when(mockMessage.getChatId()).thenReturn(chatId);
        when(mockMessage.isCommand()).thenReturn(true);
        when(mockMessage.getText()).thenReturn(content);
        when(mockMessage.getFrom()).thenReturn(mockUser);
        Update mockUpdate = mock(Update.class);
        when(mockUpdate.hasMessage()).thenReturn(true);
        when(mockUpdate.getMessage()).thenReturn(mockMessage);
        return mockUpdate;
    }
}
