package com.voucher.global.common.error;

import com.voucher.global.common.error.exeption.VoucherPointException;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	protected ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
		log.error("handleRuntimeException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConversionFailedException e) {
        log.info("handleConflict : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException e) {
        log.info("handleNoHandlerFoundException : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.HANDLE_NOT_FOUND_EXCEPTION);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ErrorResponse> handleServletException(ServletException e) {
        log.info("handleServletException : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.SERVLET_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        log.info("handleConstraintViolation : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.MISSING_REQUEST_VALUE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAccessException.class)
    protected ResponseEntity<ErrorResponse> handleResourceAccessException(ResourceAccessException e) {
        log.info("handleResourceAccessException : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.RESOURCE_ACCESS_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.info("handleHttpMediaTypeNotSupportedException : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
        return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

	@ExceptionHandler(MultipartException.class)
    protected ResponseEntity<ErrorResponse> handleMultipartException(MultipartException e) {
        log.info("handleMultipartException : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_FILE_STORAGE_EXCEPTION);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

	@ExceptionHandler(NoSuchElementException.class)
	protected ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
		log.info("handleNoSuchElementException : ", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.NO_SUCH_ELEMENT_EXCEPTION);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        log.info("handleMissingServletRequestParameter : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.MISSING_REQUEST_VALUE, ErrorResponse.FieldError.of(e.getParameterName(), null, e.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
    protected ResponseEntity<ErrorResponse> handleConflict(RuntimeException e) {
        log.info("handleConflict : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
        return  new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    protected ResponseEntity<ErrorResponse> handleMissingPathVariable(MissingPathVariableException e) {
        log.info("handleMissingPathVariable : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.MISSING_REQUEST_VALUE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.info("handleBindException : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, ErrorResponse.FieldError.of(e.getBindingResult()));
        return  new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.info("handleHttpMessageNotReadableException : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
        return  new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.info("handleDataIntegrityViolationException : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.DATA_INTEGRITY_VIOLATION_EXCEPTION);
        return  new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JpaSystemException.class)
    protected ResponseEntity<ErrorResponse> handleJpaSystemException(JpaSystemException e) {
        log.info("handleJpaSystemException : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
        return  new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e) {
        log.info("handleInvalidDataAccessApiUsageException : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
        return  new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * javax.validation.Valid or @Validated 으로 binding error 발생시 발생한다.
     * HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
     * 주로 @RequestBody, @RequestPart 어노테이션에서 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentTypeMismatchException e) {
		log.error("handleMethodArgumentTypeMismatchException", e);
		final ErrorResponse response = ErrorResponse.of(e);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	/**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }


    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public final ResponseEntity<ErrorResponse> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
        log.info("handleHttpMediaTypeNotAcceptableException : ", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(VoucherPointException.class)
    protected ResponseEntity<ErrorResponse> handleVoucherPointException(final VoucherPointException e) {
        log.error("handleVoucherPointException", e);
        final String errorMessage = e.getMessage();
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(errorCode, errorMessage);
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<?> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
		log.error("handleMissingServletRequestPartException", e);
		final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}
