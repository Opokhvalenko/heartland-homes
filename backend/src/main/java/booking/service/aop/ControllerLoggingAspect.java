package booking.service.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class ControllerLoggingAspect {

    private static final Logger logger = LogManager.getLogger(ControllerLoggingAspect.class);

    @Around("within(booking.service.controller..*) && "
            + "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object logHttpRequests(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request =
                    ((ServletRequestAttributes) requestAttributes).getRequest();

            logger.info("HTTP {} {} від IP: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    getClientIpAddress(request));
        }

        return joinPoint.proceed();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String requestHeader = request.getHeader("X-Forwarded-For");
        if (requestHeader != null && !requestHeader.isEmpty()) {
            return requestHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
