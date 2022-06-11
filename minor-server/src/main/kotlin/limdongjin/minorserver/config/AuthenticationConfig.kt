package limdongjin.minorserver.config

import limdongjin.minorserver.security.LoginUserResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

//@Configuration
//class AuthenticationConfig(
//    private val loginUserResolver: LoginUserResolver
//) : WebFluxConfigurer {
//    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
//        configurer.addCustomResolver(loginUserResolver)
//        super.configureArgumentResolvers(configurer)
//    }
//}