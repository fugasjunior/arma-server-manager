package cz.forgottenempire.servermanager.keymgmt

import org.assertj.core.api.Assertions.assertThatNoException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile

class BiKeyValidatorTest {

    private val validator = BiKeyValidator()

    /** Minimal valid payload: file named *.bikey containing the RSA1 magic marker. */
    private fun validBikey(name: String = "mykey.bikey") = MockMultipartFile(
        "file", name, MediaType.APPLICATION_OCTET_STREAM_VALUE,
        "some_prefix_RSA1_some_suffix".toByteArray()
    )

    @Test
    fun validate_validBikey_doesNotThrow() {
        assertThatNoException().isThrownBy { validator.validate(validBikey()) }
    }

    @Test
    fun validate_wrongExtension_throwsInvalidBiKeyException() {
        val file = MockMultipartFile(
            "file", "mykey.txt", MediaType.TEXT_PLAIN_VALUE,
            "some_prefix_RSA1_some_suffix".toByteArray()
        )
        assertThatThrownBy { validator.validate(file) }
            .isInstanceOf(InvalidBiKeyException::class.java)
            .hasMessageContaining("not a .bikey file")
    }

    @Test
    fun validate_emptyFile_throwsInvalidBiKeyException() {
        val file = MockMultipartFile(
            "file", "mykey.bikey", MediaType.APPLICATION_OCTET_STREAM_VALUE, ByteArray(0)
        )
        assertThatThrownBy { validator.validate(file) }
            .isInstanceOf(InvalidBiKeyException::class.java)
            .hasMessageContaining("empty")
    }

    @Test
    fun validate_missingRsa1Marker_throwsInvalidBiKeyException() {
        val file = MockMultipartFile(
            "file", "mykey.bikey", MediaType.APPLICATION_OCTET_STREAM_VALUE,
            "XXXXX_no_marker_here_XXXXX".toByteArray()
        )
        assertThatThrownBy { validator.validate(file) }
            .isInstanceOf(InvalidBiKeyException::class.java)
            .hasMessageContaining("RSA1")
    }

    @Test
    fun validate_nullFilename_throwsInvalidBiKeyException() {
        val file = MockMultipartFile(
            "file", null, MediaType.APPLICATION_OCTET_STREAM_VALUE,
            "RSA1".toByteArray()
        )
        assertThatThrownBy { validator.validate(file) }
            .isInstanceOf(InvalidBiKeyException::class.java)
    }
}
