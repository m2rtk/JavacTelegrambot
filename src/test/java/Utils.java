import javac.ClassFile;
import javac.JavaFile;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Utils {

    private static int updateId = 0;

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
        when(mockUpdate.getUpdateId()).thenReturn(updateId++);
        return mockUpdate;
    }

    static Update createMockUpdate(Long chatId) {
        Message mockMessage = mock(Message.class);
        when(mockMessage.getChatId()).thenReturn(chatId);
        Update mockUpdate = mock(Update.class);
        when(mockUpdate.hasMessage()).thenReturn(true);
        when(mockUpdate.getMessage()).thenReturn(mockMessage);
        when(mockUpdate.getUpdateId()).thenReturn(updateId++);
        return mockUpdate;
    }
}
