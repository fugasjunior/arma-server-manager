package cz.forgottenempire.servermanager.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AttributeEncryptorTest {

    // Valid AES-256 key: exactly 32 bytes (matches what SecretsEnvironmentPostProcessor generates)
    private static final String KEY = "0123456789abcdef0123456789abcdef";

    @Test
    void whenEncryptionDisabled_thenValuePassesThroughUnchanged() throws Exception {
        AttributeEncryptor encryptor = new AttributeEncryptor(null);

        assertThat(encryptor.convertToDatabaseColumn("secret")).isEqualTo("secret");
        assertThat(encryptor.convertToEntityAttribute("secret")).isEqualTo("secret");
    }

    @Test
    void whenEncryptionEnabled_thenRoundTripReturnsOriginal() throws Exception {
        AttributeEncryptor encryptor = new AttributeEncryptor(KEY);

        String encrypted = encryptor.convertToDatabaseColumn("my-password");

        assertThat(encrypted).isNotEqualTo("my-password");
        assertThat(encryptor.convertToEntityAttribute(encrypted)).isEqualTo("my-password");
    }

    @Test
    void whenReadingLegacyPlaintext_thenReturnedUnchangedInsteadOfThrowing() throws Exception {
        AttributeEncryptor encryptor = new AttributeEncryptor(KEY);

        // Value stored as plain text before encryption was enabled
        String legacyPlaintext = "legacy-plaintext-password";

        assertThat(encryptor.convertToEntityAttribute(legacyPlaintext)).isEqualTo(legacyPlaintext);
    }

    @Test
    void whenReadingNullOrBlank_thenReturnedUnchanged() throws Exception {
        AttributeEncryptor encryptor = new AttributeEncryptor(KEY);

        assertThat(encryptor.convertToEntityAttribute(null)).isNull();
        assertThat(encryptor.convertToEntityAttribute("")).isEqualTo("");
        assertThat(encryptor.convertToEntityAttribute("   ")).isEqualTo("   ");
    }
}
