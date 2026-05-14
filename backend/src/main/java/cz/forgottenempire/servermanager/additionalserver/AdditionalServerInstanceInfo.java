package cz.forgottenempire.servermanager.additionalserver;

import java.time.LocalDateTime;

record AdditionalServerInstanceInfo(long id, boolean alive, LocalDateTime startedAt, Process process) {}
