package dao;

import javac.BackgroundJavaProcess;
import javac.ClassFile;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created on 06.04.2017.
 */
public class WriteToDiskBotDAO implements BotDAO {
    private static final BackgroundJavaProcessesDAO bjpDAO = new BackgroundJavaProcessesDAO();
    private static final String TAG = "TODISKDAO";
    private static final String DIR = "cache";

    @Override
    public void add(ClassFile classFile, Long id, Privacy privacy) {
        try {
            Files.createDirectories(getFolderPath(id, privacy));
            Files.write(getFilePath(classFile.getClassName(), id, privacy), classFile.getByteCode());
        } catch (IOException e) {
            BotLogger.error(TAG, e);
        }
    }

    @Override
    public boolean remove(String className, Long id, Privacy privacy) {
        if (Files.exists(getFilePath(className, id, privacy))) {
            try {
                Files.delete(getFilePath(className, id, privacy));
                return true;
            } catch (IOException e) {
                BotLogger.error(TAG, e);
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty(Long id, Privacy privacy) {
        if (Files.exists(getFolderPath(id, privacy))) {
            try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(getFolderPath(id, privacy))) {
                return !dirStream.iterator().hasNext();
            } catch (IOException e) {
                BotLogger.error(TAG, e);
            }
        }
        return true;
    }

    @Override
    public boolean contains(String className, Long id, Privacy privacy) {
        return Files.exists(getFilePath(className, id, privacy));
    }

    @Override
    public Set<ClassFile> getAll(Long id, Privacy privacy) {
        Set<ClassFile> result = new HashSet<>();
        ClassFile classFile;
        if (Files.exists(getFolderPath(id, privacy))) {
            try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(getFolderPath(id, privacy))) {
                for (Path path : dirStream) {
                    classFile = new ClassFile(path.toFile().getName().replace(".class", ""), Files.readAllBytes(path));
                    result.add(classFile);
                }
            } catch (IOException e) {
                BotLogger.error(TAG, e);
            }
        }
        return result;
    }

    @Override
    public ClassFile get(String className, Long id, Privacy privacy) {
        if (Files.exists(getFilePath(className, id, privacy))) {
            try {
                return new ClassFile(className, Files.readAllBytes(getFilePath(className, id, privacy)));
            } catch (IOException e) {
                BotLogger.error(TAG, e);
            }
        }
        return null;
    }


    @Override
    public void addJavaProcess(BackgroundJavaProcess process, long chatId) {
        bjpDAO.addJavaProcess(process, chatId);
    }

    @Override
    public boolean removeJavaProcess(int pid, long chatId) {
        return bjpDAO.removeJavaProcess(pid, chatId);
    }

    @Override
    public BackgroundJavaProcess getJavaProcess(int pid, long chatId) {
        return bjpDAO.getJavaProcess(pid, chatId);
    }

    @Override
    public Map<Integer, BackgroundJavaProcess> getAllJavaProcesses(long chatId) {
        return bjpDAO.getAllJavaProcesses(chatId);
    }

    private Path getFilePath(String name, Long id, Privacy privacy) {
        return Paths.get(DIR + "/" + privacy + "/" + id + "/" + name + ".class");
    }

    private Path getFolderPath(Long id, Privacy privacy) {
        return Paths.get(DIR + "/" + privacy + "/" + id);
    }
}
