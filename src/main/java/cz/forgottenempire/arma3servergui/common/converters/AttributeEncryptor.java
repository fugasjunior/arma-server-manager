package cz.forgottenempire.arma3servergui.common.converters;

import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Converter
@Configurable
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private final boolean encryptionEnabled;
    private Key key;
    private Cipher cipher;

    public AttributeEncryptor(@Value("${database.encryption.secret:#{null}}") String secret) throws Exception {
        if (!StringUtils.isBlank(secret)) {
            encryptionEnabled = true;
            key = new SecretKeySpec(secret.getBytes(), "AES");
            cipher = Cipher.getInstance("AES");
        } else {
            encryptionEnabled = false;
        }
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if(!encryptionEnabled) {
            return attribute;
        }

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if(!encryptionEnabled) {
            return dbData;
        }

        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalStateException(e);
        }
    }
}
