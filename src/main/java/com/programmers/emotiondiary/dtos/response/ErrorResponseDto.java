package com.programmers.emotiondiary.dtos.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Builder
@Getter
public class ErrorResponseDto {
    private int status;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseDto> toResponseEntity(NoSuchElementException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDto.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .code("NoSuchElementException")
                        .message(e.getMessage())
                        .build());
    }

    public static ResponseEntity<ErrorResponseDto> toResponseEntity(MethodArgumentNotValidException errorCode) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ErrorResponseDto.builder()
                        .status(BAD_REQUEST.value())
                        .code("MethodArgumentNotValidException")
                        .message(errorCode.getFieldError().getDefaultMessage())
                        .build());
    }

    public static ResponseEntity<ErrorResponseDto> toResponseEntity(IllegalArgumentException errorCode) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ErrorResponseDto.builder()
                        .status(BAD_REQUEST.value())
                        .code("MethodArgumentNotValidException")
                        .message(errorCode.getMessage())
                        .build());
    }

}
