package com.authenfication.api.application.advice;

import com.authenfication.api.application.exception.CCommunicationException;
import com.authenfication.api.application.exception.CEmailSigninFailedException;
import com.authenfication.api.application.exception.CUserNotFoundException;
import com.authenfication.api.application.web.dto.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final MessageSource messageSource;


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseMessage defaultException(HttpServletRequest request, Exception e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage(getMessage("unKnown.msg"));
        return responseMessage;
    }

    @ExceptionHandler(CUserNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseMessage cUserNotFoundException(HttpServletRequest request, CUserNotFoundException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage(getMessage("userNotFound.msg"));
        return responseMessage;
    }

    @ExceptionHandler(CCommunicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseMessage cCommunicationException(HttpServletRequest request, CCommunicationException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage(getMessage("communicationError.msg"));
        return responseMessage;
    }

    @ExceptionHandler(CEmailSigninFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseMessage cEmailSigninFailedException(HttpServletRequest request, CEmailSigninFailedException e) {
        // 예외 처리의 메시지를 MessageSource에서 가져오도록 수정
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage(getMessage("emailSigninFailed.msg"));
        return responseMessage;
    }

    // code정보에 해당하는 메시지를 조회합니다.
    private String getMessage(String code) {
        return getMessage(code, null);
    }

    // code정보, 추가 argument로 현재 locale에 맞는 메시지를 조회합니다.
    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

}
