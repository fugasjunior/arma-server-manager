package cz.forgottenempire.servermanager.applicationlog

import cz.forgottenempire.servermanager.common.PathsFactory
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.io.File
import java.nio.file.Path

@ExtendWith(MockitoExtension::class)
class ApplicationLogControllerTest {

    @Mock(stubOnly = true)
    private lateinit var pathsFactory: PathsFactory

    @TempDir
    private lateinit var tempDir: Path

    private lateinit var controller: ApplicationLogController

    @BeforeEach
    fun setUp() {
        controller = ApplicationLogController(pathsFactory)
    }

    @Test
    fun `getApplicationLog returns 200 with log content`() {
        val logFile = tempDir.resolve("spring-boot-logger.log").toFile()
        logFile.writeText("Line 1\nLine 2\nLine 3\n")
        `when`(pathsFactory.applicationLogFile).thenReturn(logFile)

        val response = controller.getApplicationLog(count = 3)

        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        assertThat(response.body).isNotEmpty()
    }

    @Test
    fun `downloadApplicationLog throws NotFoundException when log file does not exist`() {
        `when`(pathsFactory.applicationLogFile).thenReturn(File(tempDir.toFile(), "nonexistent.log"))

        assertThatThrownBy { controller.downloadApplicationLog() }
            .isInstanceOf(NotFoundException::class.java)
            .hasMessage("Application log file doesn't exist")
    }
}
