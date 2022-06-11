package limdongjin.minorserver.application

import limdongjin.minorserver.domain.user.Gender
import limdongjin.minorserver.domain.user.Password
import limdongjin.minorserver.domain.user.User
import java.time.LocalDate
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Past
import javax.validation.constraints.Pattern

data class SearchRequestDto(
    @field:NotBlank
    val query: String
)