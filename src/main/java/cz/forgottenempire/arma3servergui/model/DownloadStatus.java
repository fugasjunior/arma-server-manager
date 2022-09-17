package cz.forgottenempire.arma3servergui.model;

import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import lombok.Data;

@Data
@Embeddable
public class DownloadStatus {

    private Date timestamp;
    private boolean success;
    @Enumerated(EnumType.STRING)
    private ErrorStatus errorStatus;
    @Transient
    private Throwable exception;

    public DownloadStatus() {
        timestamp = new Date();
        errorStatus = null;
    }

    public DownloadStatus(boolean success) {
        this();
        this.success = success;
    }

    public DownloadStatus(ErrorStatus errorStatus, Throwable exception) {
        this();
        this.success = false;
        this.errorStatus = errorStatus;
        this.exception = exception;
    }

    public enum ErrorStatus {
        WRONG_AUTH,
        IO,
        TIMEOUT,
        NO_MATCH,
        NO_SUBSCRIPTION,
        GENERIC
    }
}
