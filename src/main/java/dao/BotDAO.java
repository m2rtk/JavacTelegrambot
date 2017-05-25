package dao;

import javac.Compiled;
import java.util.Set;

/**
 * todo replace this with a static write to disk class. Maybe
 */
public interface BotDAO {

    void add(Compiled compiled, Long id, Privacy privacy);

    boolean remove(String name, Long id, Privacy privacy);

    boolean isEmpty(Long id, Privacy privacy);

    boolean contains(String name, Long id, Privacy privacy);

    Set<Compiled> getAll(Long id, Privacy privacy);

    Compiled get(String name, Long id, Privacy privacy);
}
