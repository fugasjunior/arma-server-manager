package cz.forgottenempire.servermanager.applicationlog

import cz.forgottenempire.servermanager.api.ApplicationLogApi
import cz.forgottenempire.servermanager.common.PathsFactory
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException
import cz.forgottenempire.servermanager.security.permission.PermissionCode
import cz.forgottenempire.servermanager.serverinstance.LogFile
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("hasAuthority('${PermissionCode.APPLICATION_LOGS_VIEW}')")
class ApplicationLogController(private val pathsFactory: PathsFactory) : ApplicationLogApi {

    override fun getApplicationLog(count: Int?): ResponseEntity<String> {
        val logFile = LogFile(pathsFactory.applicationLogFile)
        return ResponseEntity.ok(logFile.getLastLines(count ?: 100))
    }

    override fun downloadApplicationLog(): ResponseEntity<Resource> {
        val logFile = LogFile(pathsFactory.applicationLogFile)
        val resource = logFile.asResource()
            .orElseThrow { NotFoundException("Application log file doesn't exist") }
        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${resource.file.name}")
        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(resource.contentLength())
            .contentType(MediaType.TEXT_PLAIN)
            .body(resource)
    }
}
