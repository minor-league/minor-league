package limdongjin.minorserver.config

import limdongjin.minorserver.domain.user.UserRepository
import limdongjin.minorserver.domain.user.findByEmail
import limdongjin.minorserver.security.CustomUserDetails
import limdongjin.minorserver.security.JwtTokenAuthenticationFilter
import limdongjin.minorserver.security.JwtTokenProvider
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.web.reactive.config.EnableWebFlux
import reactor.core.publisher.Mono
import java.security.Permission


//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
//import org.springframework.security.config.web.servlet.invoke

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
open class SecurityConfig(val applicationContext: ApplicationContext) { // : WebSecurityConfigurerAdapter() {

    @Bean
    @DependsOn("methodSecurityExpressionHandler")
    fun apiHttpSecurity(http: ServerHttpSecurity,
                        jwtTokenProvider: JwtTokenProvider,
                        reactiveAuthenticationManager: ReactiveAuthenticationManager
                        ): SecurityWebFilterChain {
        val handler = applicationContext.getBean(DefaultMethodSecurityExpressionHandler::class.java)
        handler.setPermissionEvaluator(myPermissionEvaluator());

        return http
            .securityMatcher(PathPatternParserServerWebExchangeMatcher("/api/**"))
            .exceptionHandling {
                it.authenticationEntryPoint { exchange, ex ->
                    Mono.fromRunnable {
                        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                    }
                }.accessDeniedHandler { exchange, denied ->
                    Mono.fromRunnable {
                        exchange.response.statusCode = HttpStatus.FORBIDDEN
                    }
                }
            }
            .cors().disable()
            .csrf().disable() // note.
            .formLogin().disable()
            .httpBasic().disable()
            .authenticationManager(reactiveAuthenticationManager)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/api/user/login").permitAll()
                    .pathMatchers("/api/user/register").permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(JwtTokenAuthenticationFilter(jwtTokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
//            .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt)
            .build()

    }


    @Bean
    fun userDetailsService(userRepository: UserRepository): ReactiveUserDetailsService {
        return ReactiveUserDetailsService { username ->
            val user = userRepository.findByEmail(username) ?: return@ReactiveUserDetailsService Mono.empty()
            val userDetails = CustomUserDetails(
                username = username,
                password = "masked",
                enabled = true,
                accountNonExpired = true,
                credentialsNonExpired = true,
                accountNonLocked = true
            )

            //                List<String> permissions = userDetails.getPermissions();
            //                usUserMaster.getUsUserRoleEntityList().stream().forEach(
            //                    usUserRoleEntity -> {
            //                    usUserRoleEntity.getUsRoleEntity().getUsRolePermissionEntityList().stream().forEach(
            //                        usRolePermissionEntity -> permissions.add(usRolePermissionEntity.getUsPermissionEntity().getPermissionCode())
            //                    );
            //                });
            Mono.just(userDetails)
        }
    }

    @Bean
    fun reactiveAuthenticationManager(userDetailsService: ReactiveUserDetailsService): ReactiveAuthenticationManager {  //, passwordEncoder: PasswordEncoder): ReactiveAuthenticationManager {
        val authenticationManager = UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
//        authenticationManager.setPasswordEncoder(passwordEncoder)

        return authenticationManager;
    }
    @Bean
    fun myPermissionEvaluator(): PermissionEvaluator {
        return object: PermissionEvaluator {
            override fun hasPermission(
                authentication: Authentication?,
                targetDomainObject: Any?,
                permission: Any?,
            ): Boolean {
                if(authentication == null){
                    println("auth null")
                    return false
                }
                if(!authentication.isAuthenticated){
                    println("auth not autent")
                    return false
                }
                println("auth true")
                return true
//                return authentication?.authorities
//                    ?.any { grantedAuthority -> grantedAuthority.authority.equals(targetDomainObject) }
//                ?: false
            }


            override fun hasPermission(authentication: Authentication, targetId: java.io.Serializable, targetType: String, permission: Any): Boolean{
                println("auth has permisssion false")
                return false
            }
        }
    }

//
//    @Bean
//    fun webHttpSecurity(http: ServerHttpSecurity): SecurityWebFilterChain? {
//        return http
//            .authorizeExchange { exchanges: AuthorizeExchangeSpec ->
//                exchanges
//                    .anyExchange().authenticated()
//            }
//            .httpBasic(withDefaults())
//            .build()
//    }
//
//    @Bean
//    fun userDetailsService(): ReactiveUserDetailsService? {
//        return MapReactiveUserDetailsService(
//            PasswordEncodedUser.user(), PasswordEncodedUser.admin()
//        )
//    }
//    override fun configure(http: HttpSecurity) {
//        http {
//            headers {
//                frameOptions { disable() }
//            }
//            csrf { disable() }
//            authorizeRequests {
//                authorize("/admin/**", authenticated)
//                authorize("/**", permitAll)
//            }
//            formLogin {
//                loginPage = "/admin/login"
//                loginProcessingUrl = "/admin/login"
//                failureUrl = "/admin/login?error"
//                permitAll = true
//                defaultSuccessUrl("/admin", false)
//            }
//            logout {
//                logoutSuccessUrl = "/admin/login"
//            }
//        }
//    }
}