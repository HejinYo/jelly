package cn.hejinyo.jelly.common.exception;

import cn.hejinyo.jelly.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

/**
 * @author : HejinYo   hejinyo@gmail.com 2017/8/13 19:12
 * @apiNote :
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandle {

    /**
     * infoException，返回消息
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(InfoException.class)
    public Result infoExceptionException(InfoException e) {
        //e.printStackTrace();
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 401 UNAUTHORIZED，无权限
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({UnauthorizedException.class})
    public Result shiroException(UnauthorizedException ex, HttpServletResponse response) {
        return Result.error(-1, "无此权限");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException e) {
        return Result.error("数据库中已存在该记录");
    }

    /**
     * 实体验证
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result validException(MethodArgumentNotValidException mnve) {
        //e.printStackTrace();
        return Result.error(mnve.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 500 Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {Exception.class})
    public Result exception(Exception ex, HttpServletResponse response) {
        if (ex instanceof HttpMessageNotReadableException) {
            return Result.error(HttpStatus.BAD_REQUEST.getReasonPhrase());
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            return Result.error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            return Result.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
        } else {
            ex.printStackTrace();
        }
        log.error("系统发生未知错误异常", ex);
        return Result.error("未知错误:" + ex.getMessage());
    }
}
