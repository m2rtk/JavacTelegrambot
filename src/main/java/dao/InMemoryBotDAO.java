package dao;

import javac.Compiled;

import java.util.*;

/**
 * Created on 4.04.2017.
 */
public class InMemoryBotDAO implements BotDAO {

    private Map<Long, Set<Compiled>> userClasses = new HashMap<>();
    private Map<Long, Set<Compiled>> chatClasses = new HashMap<>();


    @Override
    public void add(Compiled compiled, Long id, Privacy privacy) {
        if (compiled.getPrivacy() == null || compiled.getId() == null) compiled.setPrivacyAndId(privacy, id);
        getMap(privacy).computeIfAbsent(id, k -> new HashSet<>()).add(compiled);
    }

    @Override
    public boolean remove(String name, Long id, Privacy privacy) {
        return getMap(privacy).get(id) != null && getMap(privacy).get(id).remove(get(name, id, privacy));
    }

    @Override
    public boolean isEmpty(Long id, Privacy privacy) {
        return getMap(privacy).get(id) == null || getMap(privacy).get(id).isEmpty();
    }

    @Override
    public boolean contains(String name, Long id, Privacy privacy) {
        return getMap(privacy).get(id) != null && getMap(privacy).get(id).contains(get(name, id, privacy));
    }

    @Override
    public Set<Compiled> getAll(Long id, Privacy privacy) {
        return getMap(privacy).get(id);
    }

    @Override
    public Compiled get(String name, Long id, Privacy privacy) {
        if (getMap(privacy).get(id) != null)
            for (Compiled compiled : getMap(privacy).get(id))
                if (compiled.getName().equals(name))
                    return compiled;
        return null;
    }

    private Map<Long, Set<Compiled>> getMap(Privacy privacy) {
        if (privacy == Privacy.CHAT)      return chatClasses;
        else if (privacy == Privacy.USER) return userClasses;
        else throw new RuntimeException("Privacy can't be anything other than user or chat");
    }
}
