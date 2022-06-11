package limdongjin.minorserver.application

import limdongjin.minorserver.domain.user.Gender
import limdongjin.minorserver.domain.user.Password
import limdongjin.minorserver.domain.user.User
import java.time.LocalDate
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Past
import javax.validation.constraints.Pattern

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val gender: Gender,
    val birthday: LocalDate,
//    val phoneNumber: String,
) {
    constructor(user: User) : this(
        user.id,
        user.name,
        user.email,
        user.gender,
        user.birthday,
//        user.phoneNumber,
    )
}

data class RegisterUserRequest(
    @field:Pattern(regexp = "[가-힣a-zA-Z]{1,30}") // , message = "name is INVALID FORMATTED")
    @field:NotBlank
    val name: String,

    @field:Email
    val email: String,

    var gender: Gender = Gender.ETC,

    @field:Past
    val birthday: LocalDate,
    val password: Password,
    val confirmPassword: Password,
) {
    fun toEntity(): User {
        return User(name, email, gender, birthday, password)
    }
}

data class AuthenticateUserRequest(
    @field:Email
    val email: String,
    val password: Password
)

data class ResetPasswordRequest(
    @field:Pattern(regexp = "[가-힣a-zA-Z]{1,30}") //, message = "name is INVALID FORMATTED")
    val name: String,

    @field:Email
    val email: String,

    @field:Past
    val birthday: LocalDate
)

data class EditPasswordRequest(
    val oldPassword: Password,
    val password: Password,
    val confirmPassword: Password
)
