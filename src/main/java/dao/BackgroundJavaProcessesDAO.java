package dao;

import javac.BackgroundJavaProcess;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This is weak.
 */
public class BackgroundJavaProcessesDAO {
    private Map<Long, Map<Integer, BackgroundJavaProcess>> backgroundJavaProcesses = new HashMap<>();

    void addJavaProcess(BackgroundJavaProcess process, long chatId) {
        backgroundJavaProcesses.computeIfAbsent(chatId, k -> new HashMap<>()).put(process.getPid(), process);
    }

    boolean removeJavaProcess(int pid, long chatId) {
        return backgroundJavaProcesses.containsKey(chatId) && backgroundJavaProcesses.get(chatId).remove(pid) != null;
    }

    BackgroundJavaProcess getJavaProcess(int pid, long chatId) {
        if (backgroundJavaProcesses.containsKey(chatId)) return backgroundJavaProcesses.get(chatId).get(pid);
        return null;
    }

    Set<BackgroundJavaProcess> getAllJavaProcesses(long chatId) {
        if (backgroundJavaProcesses.containsKey(chatId)) return new HashSet<>(backgroundJavaProcesses.get(chatId).values());
        return new HashSet<>();
    }
}
