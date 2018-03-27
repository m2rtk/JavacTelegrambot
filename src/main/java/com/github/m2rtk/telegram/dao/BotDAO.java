package com.github.m2rtk.telegram.dao;

import com.github.m2rtk.telegram.javac.ClassFile;

import java.util.Set;

/**
 * todo replace this with a static write to disk class. Maybe
 */
public interface BotDAO {

    void add(ClassFile classFile, Long id, Privacy privacy);

    boolean remove(String className, Long id, Privacy privacy);

    boolean isEmpty(Long id, Privacy privacy);

    boolean contains(String className, Long id, Privacy privacy);

    Set<ClassFile> getAll(Long id, Privacy privacy);

    ClassFile get(String className, Long id, Privacy privacy);
}
