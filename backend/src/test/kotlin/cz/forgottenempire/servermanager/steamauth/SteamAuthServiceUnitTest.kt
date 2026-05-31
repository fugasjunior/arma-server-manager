package cz.forgottenempire.servermanager.steamauth

import cz.forgottenempire.servermanager.api.model.SteamAuthDto
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
        val steamAuth = SteamAuth(1L, "username", "hunter2", "ABCDE")
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
    fun `when set steam account with new password, then password is updated`() {
        val prevAuth = SteamAuth(1L, "username", "hunter2", "ABCDE")
        `when`(steamAuthRepository.findAll()).thenReturn(listOf(prevAuth))
        val newAuth = SteamAuthDto().apply {
            username = "new_username"
            password = "n3wp4ssw0rd"
            steamGuardToken = "FGHIJ"
        }

        service.setAuthAccount(newAuth)

        val actualAuthAccount = service.getAuthAccount()
        assertThat(actualAuthAccount.username).isEqualTo("new_username")
        assertThat(actualAuthAccount.password).isEqualTo("n3wp4ssw0rd")
        assertThat(actualAuthAccount.steamGuardToken).isEqualTo("FGHIJ")
    }

    @Test
    fun `when set steam account without password, then original password is kept`() {
        val prevAuth = SteamAuth(1L, "username", "hunter2", "ABCDE")
        `when`(steamAuthRepository.findAll()).thenReturn(listOf(prevAuth))
        val newAuth = SteamAuthDto().apply {
            username = "new_username"
            steamGuardToken = "FGHIJ"
        }

        service.setAuthAccount(newAuth)

        val actualAuthAccount = service.getAuthAccount()
        assertThat(actualAuthAccount.username).isEqualTo("new_username")
        assertThat(actualAuthAccount.password).isEqualTo("hunter2")
        assertThat(actualAuthAccount.steamGuardToken).isEqualTo("FGHIJ")
    }

    @Test
    fun `when clear account, then repository deleteAll method called`() {
        service.clearAuthAccount()

        verify(steamAuthRepository).deleteAll()
    }

    @Test
    fun `when is auth configured and username and password are set, then return true`() {
        val steamAuth = SteamAuth(1L, "username", "password", "ABCDE")
        `when`(steamAuthRepository.findAll()).thenReturn(listOf(steamAuth))

        val result = service.isAuthConfigured()

        assertThat(result).isTrue()
    }

    @Test
    fun `when is auth configured and username is null, then return false`() {
        val steamAuth = SteamAuth(1L, null, "password", "ABCDE")
        `when`(steamAuthRepository.findAll()).thenReturn(listOf(steamAuth))

        val result = service.isAuthConfigured()

        assertThat(result).isFalse()
    }

    @Test
    fun `when is auth configured and password is null, then return false`() {
        val steamAuth = SteamAuth(1L, "username", null, "ABCDE")
        `when`(steamAuthRepository.findAll()).thenReturn(listOf(steamAuth))

        val result = service.isAuthConfigured()

        assertThat(result).isFalse()
    }
}
