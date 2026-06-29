package cz.forgottenempire.servermanager.additionalserver;

import cz.forgottenempire.servermanager.common.ServerStatus;
import java.time.LocalDateTime;

record AdditionalServerInstanceInfo(long id, boolean alive, ServerStatus status, LocalDateTime startedAt, Process process) {}
