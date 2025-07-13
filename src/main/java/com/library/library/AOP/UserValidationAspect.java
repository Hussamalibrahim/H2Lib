package com.library.library.AOP;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserValidationAspect {

//    @Autowired
//    private UserCredentialsService userCredentialsService;
//
//    @Pointcut("execution(* com.example.SpringBootObject.Controller.*.*(..)) && args(userDetails, ..)")
//    public void methodsRequiringUserValidation(UserDetails userDetails) {}
//
//    @Around("methodsRequiringUserValidation(userDetails)")
//    public Object validateUser(ProceedingJoinPoint joinPoint, UserDetails userDetails) throws Throwable {
//        if (userCredentialsService.findByUsername(userDetails.getUsername()).isEmpty()) {
//            return new RedirectView("/loginPage");
//        }
//        return joinPoint.proceed();
//    }
}