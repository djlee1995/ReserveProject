package org.spring.reserve.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiResponse {

    private int status;

    private Object data;

    private String error;

    private String message;

    /**
     *
     */
    public ApiResponse() {
        this(HttpStatus.OK.value());
    }

    /**
     * @param status
     */
    public ApiResponse(int status) {
        super();
        this.status = status;
    }

    /**
     * @param status
     */
    public ApiResponse(HttpStatus status) {
        this(status.value());
    }

    /**
     * @param data
     */
    public ApiResponse(Object data) {
        this.status = HttpStatus.OK.value();
        this.data = data;
    }

    /**
     * @param status
     * @param error
     */
    public ApiResponse(int status, String error) {
        this.status = status;
        this.error = error;
    }

    /**
     * @param status
     * @param error
     */
    public ApiResponse(HttpStatus status, String error) {
        this.status = status.value();
        this.error = error;
    }

    public static ApiResponse ok() {
        return new ApiResponse();
    }

    public static ApiResponse ok(Object data) {
        return new ApiResponse(data);
    }

    public static ApiResponse error(HttpStatus status, String error) {
        return new ApiResponse(status, error);
    }

}


