package cz.forgottenempire.servermanager.common

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/health")
    fun health(): ResponseEntity<Void> = ResponseEntity.ok().build()
}
