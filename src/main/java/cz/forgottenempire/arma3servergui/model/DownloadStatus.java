package cz.forgottenempire.arma3servergui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import lombok.Data;

@Data
public class DownloadStatus {

    private Date timestamp;
    private boolean success;
    private ErrorStatus errorStatus;
    @JsonIgnore
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
