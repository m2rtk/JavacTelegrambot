package dao;

import javac.Compiled;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created on 06.04.2017.
 */
public class WriteToDiskBotDAO implements BotDAO {
    private static final String TAG = "TODISKDAO";
    private static final String DIR = "cache";

    @Override
    public void add(Compiled compiled, Long id, Privacy privacy) {
        try {
            Files.createDirectories(getFolderPath(id, privacy));
            Files.write(getFilePath(compiled.getName(), id, privacy), compiled.getByteCode());
        } catch (IOException e) {
            BotLogger.error(TAG, e);
        }
    }

    @Override
    public boolean remove(String name, Long id, Privacy privacy) {
        if (Files.exists(getFilePath(name, id, privacy))) {
            try {
                Files.delete(getFilePath(name, id, privacy));
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
        return false;
    }

    @Override
    public boolean contains(String name, Long id, Privacy privacy) {
        return Files.exists(getFilePath(name, id, privacy));
    }

    @Override
    public Set<Compiled> getAll(Long id, Privacy privacy) {
        Set<Compiled> result = new HashSet<>();
        if (Files.exists(getFolderPath(id, privacy))) {
            try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(getFolderPath(id, privacy))) {
                for (Path path : dirStream) {
                    result.add(new Compiled(Files.readAllBytes(path), path.toFile().getName().replace(".class", ""), privacy, id));
                }
            } catch (IOException e) {
                BotLogger.error(TAG, e);
            }
        }
        return result;
    }

    @Override
    public Compiled get(String name, Long id, Privacy privacy) {
        if (Files.exists(getFilePath(name, id, privacy))) {
            try {
                return new Compiled(Files.readAllBytes(getFilePath(name, id, privacy)), name, privacy, id);
            } catch (IOException e) {
                BotLogger.error(TAG, e);
            }
        }
        return null;
    }

    private Path getFilePath(String name, Long id, Privacy privacy) {
        return Paths.get(DIR + "/" + privacy + "/" + id + "/" + name + ".class");
    }

    private Path getFolderPath(Long id, Privacy privacy) {
        return Paths.get(DIR + "/" + privacy + "/" + id);
    }
}
