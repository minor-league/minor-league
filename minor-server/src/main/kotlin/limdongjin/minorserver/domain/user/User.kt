package limdongjin.minorserver.domain.user

import limdongjin.support.domain.BaseRootEntity
import org.springframework.security.authentication.BadCredentialsException
import java.time.LocalDate
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.security.auth.login.CredentialNotFoundException

@Entity
class User(
    @Embedded
    var information: UserInformation,

    @AttributeOverride(name = "value", column = Column(name = "password", nullable = false))
    @Embedded
    var password: Password,
    id: Long = 0L
) : BaseRootEntity<User>(id) {
    val name: String
        get() = information.name

    val email: String
        get() = information.email

//    val phoneNumber: String
//        get() = information.phoneNumber

    var gender: Gender
        get() = information.gender
        set(newGender) {
            information.gender = newGender
        }

    var birthday: LocalDate
        get() = information.birthday
        set(newBirthday){
            information.birthday = newBirthday
        }

    constructor(
        name: String,
        email: String,
        gender: Gender = Gender.UNKNOWN,
        birthday: LocalDate,
        password: Password,
        id: Long = 0L
//        phoneNumber: String,
    ) : this(UserInformation(name, email, gender, birthday), password, id)

    fun authenticate(password: Password) {
        identify(this.password == password) { "사용자 정보가 일치하지 않습니다." }

    }

    fun resetPassword(name: String, birthday: LocalDate, password: String) {
        identify(information.same(name, birthday)) { "사용자 정보가 일치하지 않습니다." }
        this.password = Password(password)
        registerEvent(PasswordResetEvent(id, name, email, password))
    }

    fun changePassword(oldPassword: Password, newPassword: Password) {
        identify(this.password == oldPassword) { "기존 비밀번호가 일치하지 않습니다." }
        this.password = newPassword
    }

    private fun identify(value: Boolean, lazyMessage: () -> Any = {}) {
        if (!value) {
            val message = lazyMessage()
            throw CredentialNotFoundException(message.toString())
        }
    }
}