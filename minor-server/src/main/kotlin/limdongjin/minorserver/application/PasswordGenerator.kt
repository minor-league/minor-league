package limdongjin.minorserver.application

import org.springframework.stereotype.Component
import java.util.*

interface PasswordGenerator {
    fun generate(): String
}

@Component
class UUIDBasedPasswordGenerator : PasswordGenerator {
    override fun generate(): String = UUID.randomUUID().toString().take(8)
}