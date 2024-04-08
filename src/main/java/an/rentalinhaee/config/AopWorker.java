//package an.rentalinhaee;

//@Configuration
//@Aspect
//public class AopWorker {
//
//    @Around("execution(public * an.rentalinhaee.controller.*Controller.*(..))")
//    public Object browserCheck(ProceedingJoinPoint point) throws RuntimeException {
//        final ServletRequestAttributes attribute = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
//        final HttpServletRequest request = Optional.ofNullable(attribute).map(ServletRequestAttributes::getRequest).orElse(null);
//        String userAgent = request.getHeader("User-Agent").toUpperCase();
//        System.out.println("userAgent = " + userAgent);
//        try {
//            Object proceed = point.proceed();
//            if(userAgent.contains("ANDROID") || userAgent.contains("TABLET") || userAgent.contains("IPAD") || userAgent.contains("MOBILE") || userAgent.contains("IPHONE")) {
//                proceed = "mobile/"+proceed;
//                System.out.println("proceed = " + proceed.toString());
//            }
//            return proceed;
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
//
//
//@Configuration
//@Aspect
//public class AopWorker {
//
//    @Around("execution(public * an.rentalinhaee.controller.*Controller.*(..))")
//    public Object browserCheck(ProceedingJoinPoint point) throws Throwable {
//        HttpServletRequest request =
//                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//        HttpServletResponse response =
//                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
//        String userAgent = Optional.ofNullable(request.getHeader("User-Agent")).orElse("").toUpperCase();
//        String requestURI = request.getRequestURI();
//
//        // 요청 로깅 추가
//        System.out.println("Request URI: " + requestURI);
//
//        // 모바일 사용자 감지
//        boolean isMobileUser = userAgent.contains("ANDROID") || userAgent.contains("TABLET") ||
//                userAgent.contains("IPAD") || userAgent.contains("MOBILE") ||
//                userAgent.contains("IPHONE");
//
//        // 이미 /mobile/login으로 리다이렉션되고 있거나, 모바일 사용자가 아닌 경우 처리
//        if(requestURI.startsWith("/mobile/login") || !isMobileUser) {
//            return point.proceed();
//        }
//
//        // /login 경로에 대한 요청일 때 모바일 사용자는 /mobile/login으로 리다이렉션
//        if(requestURI.equals("/login")) {
//            return "redirect:/mobile/login";
//        }
//
//        // 다른 모바일 경로에 대한 리다이렉션 로직 처리
//        // 예시 코드에서는 생략
//
//        return point.proceed();
//    }
//
//
//
//}
