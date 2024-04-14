package cz.forgottenempire.servermanager.common;

import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Converter
@Configurable
@Slf4j
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private boolean encryptionEnabled;
    private Key key;
    private Cipher cipher;

    public AttributeEncryptor(@Value("${database.encryption.secret:#{null}}") String secret) throws Exception {
        if (StringUtils.isNotBlank(secret)) {
            try {
                key = new SecretKeySpec(secret.getBytes(), "AES");
                cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                cipher.init(Cipher.DECRYPT_MODE, key);
                encryptionEnabled = true;
            } catch (InvalidKeyException e) {
                log.error("The provided AES database encryption key is not invalid, proceeding without encryption. " +
                        "(Cause: {})", e.getMessage());
                encryptionEnabled = false;
            }
        }
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (!encryptionEnabled) {
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
        if (!encryptionEnabled) {
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
