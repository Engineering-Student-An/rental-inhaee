package an.rentalinhaee;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Configuration
@Aspect
public class AopWorker {

    @Around("execution(public * an.rentalinhaee.controller.*Controller.*(..))")
    public Object browserCheck(ProceedingJoinPoint point) throws RuntimeException {
        final ServletRequestAttributes attribute = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
        final HttpServletRequest request = Optional.ofNullable(attribute).map(ServletRequestAttributes::getRequest).orElse(null);
        String userAgent = request.getHeader("User-Agent").toUpperCase();
        System.out.println("userAgent = " + userAgent);
        try {
            Object proceed = point.proceed();
            if(userAgent.contains("ANDROID") || userAgent.contains("TABLET") || userAgent.contains("IPAD") || userAgent.contains("MOBILE") || userAgent.contains("IPHONE")) {
                proceed = "mobile/"+proceed;
            }
            return proceed;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
