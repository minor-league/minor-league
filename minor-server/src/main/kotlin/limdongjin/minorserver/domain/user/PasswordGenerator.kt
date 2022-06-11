package limdongjin.minorserver.domain.user

interface PasswordGenerator {
    fun generate(): String
}