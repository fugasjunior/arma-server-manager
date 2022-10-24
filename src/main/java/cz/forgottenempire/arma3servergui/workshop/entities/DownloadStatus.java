package cz.forgottenempire.arma3servergui.workshop.entities;

import java.util.Date;
import lombok.Data;

@Data
public class DownloadStatus {

    private Date timestamp;
    private boolean success;
    private ErrorStatus errorStatus;
    private Throwable exception;

    public DownloadStatus() {
        success = true;
        timestamp = new Date();
        errorStatus = null;
    }

    public DownloadStatus(ErrorStatus errorStatus, Throwable exception) {
        this();
        success = false;
        this.errorStatus = errorStatus;
        this.exception = exception;
    }
}