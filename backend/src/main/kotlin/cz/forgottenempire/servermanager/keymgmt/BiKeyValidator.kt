package cz.forgottenempire.servermanager.keymgmt

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

/**
 * Validates that an uploaded file is a genuine Arma 3 bikey (Windows CryptoAPI RSA PUBLICKEYBLOB).
 *
 * A bikey starts with a uint32 LE key-name length, followed by the key name, then a BLOBHEADER,
 * and then an RSAPUBKEY whose magic field is the ASCII string "RSA1" (0x52 0x53 0x41 0x31).
 * The RSA1 marker is the stable identifying signature used for validation here.
 */
@Component
class BiKeyValidator {

    /** Magic bytes identifying an RSA public-key blob: ASCII "RSA1". */
    private val RSA1_MAGIC = byteArrayOf(0x52, 0x53, 0x41, 0x31)

    /**
     * @throws InvalidBiKeyException if the file is not a valid bikey.
     */
    fun validate(file: MultipartFile) {
        val name = file.originalFilename ?: throw InvalidBiKeyException("File name is missing")
        if (!name.endsWith(".bikey", ignoreCase = true)) {
            throw InvalidBiKeyException("File '$name' is not a .bikey file")
        }
        if (file.isEmpty) {
            throw InvalidBiKeyException("File '$name' is empty")
        }

        val bytes = file.bytes
        if (!containsRsa1Magic(bytes)) {
            throw InvalidBiKeyException("File '$name' does not contain a valid bikey RSA1 signature")
        }
    }

    private fun containsRsa1Magic(bytes: ByteArray): Boolean {
        val limit = bytes.size - RSA1_MAGIC.size
        for (i in 0..limit) {
            if (bytes[i] == RSA1_MAGIC[0]
                && bytes[i + 1] == RSA1_MAGIC[1]
                && bytes[i + 2] == RSA1_MAGIC[2]
                && bytes[i + 3] == RSA1_MAGIC[3]
            ) {
                return true
            }
        }
        return false
    }
}
