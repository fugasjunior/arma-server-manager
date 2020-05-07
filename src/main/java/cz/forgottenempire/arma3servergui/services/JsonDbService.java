package cz.forgottenempire.arma3servergui.services;

import java.util.Collection;

public interface JsonDbService<T> {
    T find(Long id, Class<T> cls);
    Collection<T> findAll(Class<T> cls);
    void remove(T obj, Class<T> cls);
    void save(T obj, Class<T> cls);
}
