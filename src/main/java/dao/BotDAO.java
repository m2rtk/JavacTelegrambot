package dao;

import javac.Compiled;
import java.util.Set;

/**
 * Created on 4.04.2017.
 */
public interface BotDAO {

    void add(Compiled compiled);

    boolean remove(String name, Long id, Privacy privacy);

    boolean isEmpty(Long id, Privacy privacy);

    boolean contains(String name, Long id, Privacy privacy);

    Set<Compiled> getAll(Long id, Privacy privacy);

    Compiled get(String name, Long id, Privacy privacy);

    enum Privacy {
        CHAT, USER
    }
}
