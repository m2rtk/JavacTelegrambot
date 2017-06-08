import bot.JavaBot;
import bot.commands.visitors.DAOVisitor;
import dao.BotDAO;
import dao.Privacy;
import javac.ClassFile;
import javac.JavaFile;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

class Utils {

    private Utils() {} // prevent instantiation

    static JavaFile readJavaFile(String className) throws Exception {
        String source = String.join("\n", Files.readAllLines(Paths.get(Utils.class.getClassLoader().getResource("src/" + className + ".java").toURI())).stream().collect(Collectors.toList()));
        return new JavaFile(source);
    }

    static ClassFile readClassFile(String className) throws Exception {
        byte[] byteCode = Files.readAllBytes(Paths.get(Utils.class.getClassLoader().getResource("out/" + className + ".class").toURI()));
        return new ClassFile(className, byteCode);
    }

    static Update createMockUpdateWithTextContent(String content, Long userId, Long chatId) {
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId.intValue());
        Message mockMessage = mock(Message.class);
        when(mockMessage.getChatId()).thenReturn(chatId);
        when(mockMessage.isCommand()).thenReturn(content.trim().charAt(0) == '/');
        when(mockMessage.getText()).thenReturn(content);
        when(mockMessage.getFrom()).thenReturn(mockUser);
        Update mockUpdate = mock(Update.class);
        when(mockUpdate.hasMessage()).thenReturn(true);
        when(mockUpdate.getMessage()).thenReturn(mockMessage);
        return mockUpdate;
    }
}
