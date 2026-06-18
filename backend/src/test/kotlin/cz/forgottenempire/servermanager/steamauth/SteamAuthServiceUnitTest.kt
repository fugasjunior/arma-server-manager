package cz.forgottenempire.servermanager.steamauth

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SteamAuthServiceUnitTest {

    @Mock
    private lateinit var steamAuthRepository: SteamAuthRepository

    private lateinit var service: SteamAuthService

    @BeforeEach
    fun setUp() {
        service = SteamAuthService(steamAuthRepository)
    }

    @Test
    fun `when get auth account and steam auth setup, then return correct steam auth`() {
        val steamAuth = SteamAuth(1L, "username", "hunter2")
        `when`(steamAuthRepository.findAll()).thenReturn(listOf(steamAuth))

        val authAccount = service.getAuthAccount()

        assertThat(authAccount).isEqualTo(steamAuth)
    }

    @Test
    fun `when get auth account and no steam auth setup, then return empty steam auth`() {
        `when`(steamAuthRepository.findAll()).thenReturn(emptyList())

        val authAccount = service.getAuthAccount()

        assertThat(authAccount).isEqualTo(SteamAuth())
    }

    @Test
    fun `when save credentials, then username and password are persisted`() {
        val prevAuth = SteamAuth(1L, "username", "hunter2")
        `when`(steamAuthRepository.findAll()).thenReturn(listOf(prevAuth))

        service.saveCredentials("new_username", "n3wp4ssw0rd")

        val actualAuthAccount = service.getAuthAccount()
        assertThat(actualAuthAccount.username).isEqualTo("new_username")
        assertThat(actualAuthAccount.password).isEqualTo("n3wp4ssw0rd")
    }

    @Test
    fun `when clear account, then repository deleteAll method called`() {
        service.clearAuthAccount()

        verify(steamAuthRepository).deleteAll()
    }

    @Test
    fun `when is auth configured and username and password are set, then return true`() {
        val steamAuth = SteamAuth(1L, "username", "password")
        `when`(steamAuthRepository.findAll()).thenReturn(listOf(steamAuth))

        val result = service.isAuthConfigured()

        assertThat(result).isTrue()
    }

    @Test
    fun `when is auth configured and username is null, then return false`() {
        val steamAuth = SteamAuth(1L, null, "password")
        `when`(steamAuthRepository.findAll()).thenReturn(listOf(steamAuth))

        val result = service.isAuthConfigured()

        assertThat(result).isFalse()
    }

    @Test
    fun `when is auth configured and password is null, then return false`() {
        val steamAuth = SteamAuth(1L, "username", null)
        `when`(steamAuthRepository.findAll()).thenReturn(listOf(steamAuth))

        val result = service.isAuthConfigured()

        assertThat(result).isFalse()
    }
}
