package booking.service.aop;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LogManager.getLogger(LoggingAspect.class);
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "token", "secret", "key", "credential"
    );

    @Around(
            "within(booking.service.controller..*) "
                    + "|| within(booking.service.service..*) "
                    + "|| (within(booking.service.repository..*) && "
                    + "   !execution(* org.springframework.data.repository.Repository+.*(..)))"
    )
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();

        if (logger.isDebugEnabled()) {
            logger.debug("Початок виконання методу: {} з аргументами: {}",
                    methodName, sanitizeArguments(joinPoint.getArgs()));
        }

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            if (logger.isInfoEnabled()) {
                logger.info("Метод {} виконано за {} мс", methodName, duration);

                if (logger.isDebugEnabled() && result != null) {
                    logger.debug("Результат методу {}: {}", methodName, sanitizeResult(result));
                }
            }

            return result;

        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Метод {} завершився помилкою за {} мс: {} - {}",
                    methodName, duration, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }

    @AfterThrowing(
            pointcut = "within(booking.service.service..*)",
            throwing = "ex"
    )
    public void logCriticalErrors(JoinPoint joinPoint, Throwable ex) {
        if (ex instanceof RuntimeException && !(ex instanceof IllegalArgumentException)) {
            logger.error("КРИТИЧНА ПОМИЛКА в методі {}: ",
                    joinPoint.getSignature().toShortString(), ex);
        }
    }

    private Object[] sanitizeArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return args;
        }

        return Arrays.stream(args)
                .map(this::sanitizeObject)
                .toArray();
    }

    private Object sanitizeResult(Object result) {
        if (result == null) {
            return null;
        }

        if (result instanceof Collection) {
            return String.format("Collection[size=%d]", ((Collection<?>) result).size());
        }

        if (result instanceof Map) {
            return String.format("Map[size=%d]", ((Map<?, ?>) result).size());
        }

        return sanitizeObject(result);
    }

    private Object sanitizeObject(Object obj) {
        if (obj == null) {
            return null;
        }

        String objString = obj.toString();

        if (containsSensitiveData(objString)) {
            return String.format("%s[SANITIZED]", obj.getClass().getSimpleName());
        }

        if (objString.length() > 200) {
            return objString.substring(0, 200) + "...";
        }

        return obj;
    }

    private boolean containsSensitiveData(String str) {
        if (str == null) {
            return false;
        }

        String lowerStr = str.toLowerCase();
        return SENSITIVE_FIELDS.stream()
                .anyMatch(lowerStr::contains);
    }
}
