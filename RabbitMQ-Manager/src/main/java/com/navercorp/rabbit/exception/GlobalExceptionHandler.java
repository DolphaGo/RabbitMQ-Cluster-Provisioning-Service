package com.navercorp.rabbit.exception;

import com.navercorp.rabbit.exception.cluster.ClusterNotExistException;
import com.navercorp.rabbit.exception.common.CanNotAllowMethodException;
import com.navercorp.rabbit.exception.common.CanNotAllowRequestException;
import com.navercorp.rabbit.exception.loadbalancer.CanNotCreateLoadBalancerException;
import com.navercorp.rabbit.exception.loadbalancer.CanNotDeleteLoadBalancerException;
import com.navercorp.rabbit.exception.scale.ScaleInException;
import com.navercorp.rabbit.exception.scale.ScaleInRunningException;
import com.navercorp.rabbit.exception.scale.ScaleOutException;
import com.navercorp.rabbit.exception.scale.ScaleOutRunningException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ScaleInRunningException.class)
    protected ResponseEntity<ErrorResponse> handleScaleInRunningException(ScaleInRunningException e) {
        log.error("ScaleInRunningException", e);
        final ErrorResponse errorResponse = new ErrorResponse(ErrorCode.SCALEIN_IS_RUNNING);
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ScaleOutRunningException.class)
    protected ResponseEntity<ErrorResponse> handleScaleOutRunningException(ScaleOutRunningException e) {
        log.error("ScaleOutRunningException", e);
        final ErrorResponse errorResponse = new ErrorResponse(ErrorCode.SCALEOUT_IS_RUNNING);
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }



    @ExceptionHandler(CanNotAllowMethodException.class)
    protected ResponseEntity<ErrorResponse> handleCanNotAllowMethodException(CanNotAllowMethodException e) {
        log.error("CanNotAllowMethodException", e);
        final ErrorResponse errorResponse = new ErrorResponse(ErrorCode.CANNOT_ALLOW_METHOD);
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(CanNotAllowRequestException.class)
    protected ResponseEntity<ErrorResponse> handleCanNotAllowRequestException(CanNotAllowRequestException e) {
        log.error("CanNotAllowRequestException", e);
        final ErrorResponse errorResponse = new ErrorResponse(ErrorCode.CANNOT_ALLOW_REQUEST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ClusterNotExistException.class)
    protected ResponseEntity<ErrorResponse> handleClusterNotExistException(ClusterNotExistException e) {
        log.error("ClusterNotExistException", e);
        final ErrorResponse errorResponse = new ErrorResponse(ErrorCode.CLUSTER_NOT_EXIST);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CanNotCreateLoadBalancerException.class)
    protected ResponseEntity<ErrorResponse> handleCanNotCreateLoadBalancerException(CanNotCreateLoadBalancerException e) {
        log.error("CanNotCreateLoadBalancerException", e);
        final ErrorResponse errorResponse = new ErrorResponse(ErrorCode.CANNOT_CREATE_LOADBALANCER);
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(CanNotDeleteLoadBalancerException.class)
    protected ResponseEntity<ErrorResponse> handleCanNotDeleteLoadBalancerException(CanNotDeleteLoadBalancerException e) {
        log.error("CanNotDeleteLoadBalancerException", e);
        final ErrorResponse errorResponse = new ErrorResponse(ErrorCode.CANNOT_DELETE_LOADBALANCER);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ScaleInException.class)
    protected ResponseEntity<ErrorResponse> handleCanNotDeleteLoadBalancerException(ScaleInException e) {
        log.error("ScaleInException", e);
        final ErrorResponse errorResponse = new ErrorResponse(ErrorCode.CANNOT_DO_SCALEIN);
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ScaleOutException.class)
    protected ResponseEntity<ErrorResponse> handleCanNotDeleteLoadBalancerException(ScaleOutException e) {
        log.error("ScaleOutException", e);
        final ErrorResponse errorResponse = new ErrorResponse(ErrorCode.CANNOT_DO_SCALEOUT);
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

}