package com.featherworld.project.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(exception = NoResourceFoundException.class)
    public String notFound() {
        return "error/404";
    }

    // 프로젝트에서 발생하는 모든 종류의 예외를 잡아 처리하는 메서드
    @ExceptionHandler(exception = Exception.class)
    public String allExceptionHandler(Exception e) {

        log.error(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));

        return "error/500";
    }
}
