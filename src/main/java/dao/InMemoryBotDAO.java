package dao;

import java.Compiled;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created on 4.04.2017.
 */
public class InMemoryBotDAO implements BotDAO {

    private Map<Long, Map<String, Compiled>> userClasses = new HashMap<>();
    private Map<Long, Map<String, Compiled>> chatClasses = new HashMap<>();

    @Override
    public void add(Compiled compiled, Long id,  Privacy privacy) {

    }

    @Override
    public boolean remove(String name, Long id, Privacy privacy) {
        return false;
    }

    @Override
    public boolean isEmpty(Long id, Privacy privacy) {
        return false;
    }

    @Override
    public boolean contains(String name, Long id, Privacy privacy) {
        return false;
    }

    @Override
    public Set<String> getAll(Long id, Privacy privacy) {
        return null;
    }

    @Override
    public Compiled get(String name, Long id, Privacy privacy) {
        return null;
    }
}
