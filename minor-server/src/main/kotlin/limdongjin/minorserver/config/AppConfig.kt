package limdongjin.minorserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@Configuration
class AppConfig {
    @Bean
    fun validator(): LocalValidatorFactoryBean = LocalValidatorFactoryBean()
}