package com.meetingroom.manager.presentation.advice;

import java.util.ArrayList;
import java.util.List;

import com.meetingroom.manager.presentation.dto.ApiError;
import com.meetingroom.manager.presentation.dto.ErrorDTO;
import com.meetingroom.manager.presentation.dto.ErrorFormatCampos;
import com.meetingroom.manager.services.exception.BadRequestException;
import com.meetingroom.manager.services.exception.DataNotFoundException;
import com.meetingroom.manager.services.exception.EmptyRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;



//import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

	//Datos no encontrados
	@ExceptionHandler(value = DataNotFoundException.class) //Tipo de error que va a tratar
	public ResponseEntity<ErrorDTO> dataNotFoundException(DataNotFoundException ex){
		ErrorDTO errorDto = ErrorDTO.builder()
									  .code(ex.getCode())
									  .mensajeError(ex.getMessage())
				                      .build();
		return new ResponseEntity<ErrorDTO>(errorDto, HttpStatus.NOT_FOUND);
	}
	//controla los errores de logica o de los catch en general 400
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorFormatCampos> handlerBadRequestException(BadRequestException exception, WebRequest webRequest) {
    	ErrorFormatCampos errorFormatCampos = new ErrorFormatCampos(exception.getCode(),exception.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorFormatCampos, HttpStatus.BAD_REQUEST);
    }
    
  //controla los errores de logica o de los catch en general 400
    @ExceptionHandler(EmptyRequestException.class)
    public ResponseEntity<?> handlerBadRequestException(EmptyRequestException exception,WebRequest webRequest) {
    	ErrorFormatCampos errorFormatCampos = new ErrorFormatCampos("0", exception.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorFormatCampos, HttpStatus.BAD_REQUEST);   			          
    }
    
    /*
     *  Excepcion que se lanza cuando alguna validación de una entidad no se ha cumplido
     */
    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<String>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " + 
              violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }
    
    /*
     * Excepcion que indica que un tipo de un argumento es el no esperado.
     */
    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        @SuppressWarnings("null")
        String error = 
          ex.getName() + " deberia ser del tipo " + ex.getRequiredType().getName();

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }
    
    //@SuppressWarnings("null")
    //@Override
    /*
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> methodArgumentNotValidException(HttpServletRequest request, 
            MethodArgumentNotValidException e) {

        // get spring errors
        BindingResult result = e.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        // convert errors to standard string
        StringBuilder errorMessage = new StringBuilder();
        fieldErrors.forEach(f -> errorMessage.append(f.getField() + " " + f.getDefaultMessage() +  " "));

        // return error info object with standard json
        ApiError errorInfo = new ApiError(HttpStatus.BAD_REQUEST, request.getRequestURI(),errorMessage.toString());
        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }
    */
}
