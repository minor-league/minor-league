package limdongjin.minorserver.security

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginUser(val administrator: Boolean = false)
