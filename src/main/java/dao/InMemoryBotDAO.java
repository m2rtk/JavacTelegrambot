package dao;

import javac.BackgroundJavaProcess;
import javac.ClassFile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created on 4.04.2017.
 */
public class InMemoryBotDAO implements BotDAO {
    private static final BackgroundJavaProcessesDAO bjpDAO = new BackgroundJavaProcessesDAO();

    private Map<Long, Set<ClassFile>> userClasses = new HashMap<>();
    private Map<Long, Set<ClassFile>> chatClasses = new HashMap<>();

    @Override
    public void add(ClassFile classFile, Long id, Privacy privacy) {
        getMap(privacy).computeIfAbsent(id, k -> new HashSet<>()).add(classFile);
    }

    @Override
    public boolean remove(String className, Long id, Privacy privacy) {
        return getMap(privacy).get(id) != null && getMap(privacy).get(id).remove(get(className, id, privacy));
    }

    @Override
    public boolean isEmpty(Long id, Privacy privacy) {
        return getMap(privacy).get(id) == null || getMap(privacy).get(id).isEmpty();
    }

    @Override
    public boolean contains(String className, Long id, Privacy privacy) {
        return getMap(privacy).get(id) != null && getMap(privacy).get(id).contains(get(className, id, privacy));
    }

    @Override
    public Set<ClassFile> getAll(Long id, Privacy privacy) {
        return getMap(privacy).get(id);
    }

    @Override
    public ClassFile get(String className, Long id, Privacy privacy) {
        if (getMap(privacy).get(id) != null)
            for (ClassFile ClassFile : getMap(privacy).get(id))
                if (ClassFile.getClassName().equals(className))
                    return ClassFile;
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
    public Set<BackgroundJavaProcess> getAllJavaProcesses(long chatId) {
        return bjpDAO.getAllJavaProcesses(chatId);
    }

    private Map<Long, Set<ClassFile>> getMap(Privacy privacy) {
        if (privacy == Privacy.CHAT) return chatClasses;
        if (privacy == Privacy.USER) return userClasses;
        throw new RuntimeException("Privacy can't be anything other than user or chat");
    }
}
