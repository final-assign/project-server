package org.example.general;

import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final ResponseCode code;

    public GeneralException(ResponseCode code, String message) {

        super(message);
        this.code = code;
    }
}
