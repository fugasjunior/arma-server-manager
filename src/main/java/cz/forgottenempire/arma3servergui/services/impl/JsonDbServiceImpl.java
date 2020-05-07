package cz.forgottenempire.arma3servergui.services.impl;

import cz.forgottenempire.arma3servergui.services.JsonDbService;
import io.jsondb.JsonDBTemplate;
import io.jsondb.crypto.CryptoUtil;
import io.jsondb.crypto.Default1Cipher;
import io.jsondb.crypto.ICipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class JsonDbServiceImpl<T> implements JsonDbService<T> {

    private final Logger logger = LoggerFactory.getLogger(JsonDbServiceImpl.class);

    private final JsonDBTemplate jsonDBTemplate;

    public JsonDbServiceImpl(@Value("${config.path}") String configPath, @Value("${jsonDb.key}") String cryptoKey) {
        String baseScanPackage = "cz.forgottenempire.arma3servergui.model";

        ICipher cipher = null;
        try {
            String key = CryptoUtil.generate128BitKey(cryptoKey, "salt1234");
            cipher = new Default1Cipher(key);
        } catch (UnsupportedEncodingException | GeneralSecurityException e) {
            logger.error("Failed to set encryption for JSON DB!");
        }

        jsonDBTemplate = new JsonDBTemplate(configPath, baseScanPackage, cipher);
    }

    public synchronized void save(T obj, Class<T> cls) {
        if(!jsonDBTemplate.collectionExists(cls)) jsonDBTemplate.createCollection(cls);
        jsonDBTemplate.upsert(obj);
    }

    public synchronized void remove(T obj, Class<T> cls) {
        if(!jsonDBTemplate.collectionExists(cls)) {
            jsonDBTemplate.createCollection(cls);
            return;
        }

        jsonDBTemplate.remove(obj, cls);
    }

    public synchronized T find(Long id, Class<T> cls) {
        if(!jsonDBTemplate.collectionExists(cls)) {
            jsonDBTemplate.createCollection(cls);
            return null;
        }

        return jsonDBTemplate.findById(id, cls);
    }

    public Collection<T> findAll(Class<T> cls) {
        if(!jsonDBTemplate.collectionExists(cls)) {
            jsonDBTemplate.createCollection(cls);
            return new ArrayList<>();
        }

        return jsonDBTemplate.findAll(cls);
    }
}
