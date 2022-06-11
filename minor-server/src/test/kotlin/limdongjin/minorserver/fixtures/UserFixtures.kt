package limdongjin.minorserver.fixtures

import limdongjin.minorserver.domain.user.Gender
import limdongjin.minorserver.domain.user.Password
import limdongjin.minorserver.domain.user.User
import java.time.LocalDate

const val VALID_TOKEN1: String = "egfsiandalds"

fun createUser(
    name: String = "dongjin",
    email: String = "hello@gmail.com",
    gender: Gender = Gender.MALE,
    birthday: LocalDate = LocalDate.of(1900, 3, 12),
    password: Password = Password("1q2w3e4r!"),
    id: Long = 1L
): User {
    return User(name, email, gender, birthday, password, id)
}