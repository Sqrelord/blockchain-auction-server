package edu.dhu.auction.web.common;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.ServiceException;
import edu.dhu.auction.web.util.AssertException;
import edu.dhu.auction.web.util.ResultEntity;
import io.jsonwebtoken.JwtException;
import net.corda.core.CordaRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.exception.RequestTimeoutException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.MessagingException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.persistence.RollbackException;

import static edu.dhu.auction.web.util.ResultStatus.*;

@RestControllerAdvice
public class WebExceptionHandler {

    private static final Logger log = LogManager.getLogger(WebExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public ResultEntity<Object> accessDeniedException(Exception e) {
        e.printStackTrace();
        log.error("未登录或权限错误:{}", e.getMessage());
        return ResultEntity.build(ACCESS_DENIED);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class, HttpMessageNotReadableException.class})
    public ResultEntity<Object> requestExceptionHandler(Exception e) {
        e.printStackTrace();
        if (e instanceof HttpRequestMethodNotSupportedException) {
            log.error("请求方法错误:{}", e.getMessage());
            return ResultEntity.build(REQUEST_METHOD_ERROR);
        } else {
            log.error("参数读取失败:{}", e.getMessage());
            return ResultEntity.build(UNREADABLE_PARAMETER);
        }
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class})
    public ResultEntity<Object> methodArgumentNotValidExceptionHandler(Exception e) {
        e.printStackTrace();
        if (e instanceof MethodArgumentTypeMismatchException) {
            log.error("参数类型错误:{}", e.getMessage());
            return ResultEntity.build(ERROR_PARAMETER, e.getMessage());
        } else {
            BindingResult result = ((MethodArgumentNotValidException) e).getBindingResult();
            String message = "参数不合法";
            if (result.getFieldError() != null) {
                message = result.getFieldError().getDefaultMessage();
            }
            log.error("参数不合法:{}", message);
            return ResultEntity.fail(message);
        }
    }

    @ExceptionHandler(AssertException.class)
    public ResultEntity<Object> assertExceptionHandler(Exception e) {
        e.printStackTrace();
        log.error("验证错误:{}", e.getMessage());
        return ResultEntity.build(VERIFY_FAILURE, e.getMessage());
    }

    @ExceptionHandler({DataAccessResourceFailureException.class, DataIntegrityViolationException.class,
            ConstraintViolationException.class, RollbackException.class})
    public ResultEntity<Object> sqlExceptionHandler(Exception e) {
        e.printStackTrace();
        if (e instanceof DataAccessResourceFailureException) {
            log.error("数据库连接失败:{}", e.getMessage());
        } else if (e instanceof ConstraintViolationException || e instanceof DataIntegrityViolationException) {
            log.error("更新数据失败:{}", e.getMessage());
        } else {
            log.error("嵌套事务异常:{}", e.getMessage());
        }
        return ResultEntity.fail();
    }

    @ExceptionHandler(JwtException.class)
    public ResultEntity<Object> jwtExceptionHandler(Exception e) {
        e.printStackTrace();
        log.error("Jwt校验错误:{}", e.getMessage());
        return ResultEntity.build(JWT_ERROR, e.getMessage());
    }

    @ExceptionHandler(CordaRuntimeException.class)
    public ResultEntity<Object> cordaExceptionHandler(Exception e) {
        e.printStackTrace();
        log.error("corda错误:{}", e.getMessage());
        return ResultEntity.build(CORDA_ERROR, e.getMessage());
    }

    @ExceptionHandler({RemotingException.class, MQClientException.class,
            MQBrokerException.class, RequestTimeoutException.class, MessagingException.class})
    public ResultEntity<Object> rocketMQExceptionHandler(Exception e) {
        e.printStackTrace();
        if (e instanceof RemotingException) {
            log.error("连接失败:{}", e.getMessage());
        } else if (e instanceof MQClientException) {
            log.error("RocketMQ 客户端错误:{}", e.getMessage());
        } else if (e instanceof MQBrokerException) {
            log.error("RocketMQ Broker错误:{}", e.getMessage());
        } else if (e instanceof RequestTimeoutException) {
            log.error("消息发送超时:{}", e.getMessage());
        } else {
            log.error("消息发送失败:{}", e.getMessage());
        }
        return ResultEntity.build(ROCKET_MQ_ERROR);
    }

    @ExceptionHandler({OSSException.class, ClientException.class, ServiceException.class})
    public ResultEntity<Object> aliyunExceptionHandler(Exception e) {
        e.printStackTrace();
        if (e instanceof OSSException) {
            log.error("OSS服务器存储失败:{}", e.getMessage());
            return ResultEntity.build(OSS_ERROR);
        } else {
            log.error("Aliyun服务器连接失败:{}", e.getMessage());
        }
        return ResultEntity.build(ALIYUN_ERROR);
    }

    @ExceptionHandler
    public ResultEntity<Object> exceptionHandler(Exception e) {
        e.printStackTrace();
        log.error("操作失败:{} -> {}", e.getCause(), e.getMessage());
        return ResultEntity.build(SYSTEM_ERROR);
    }
}
