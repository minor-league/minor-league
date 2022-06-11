package limdongjin.minorserver.application

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import limdongjin.minorserver.domain.user.User
import limdongjin.minorserver.domain.user.UserRepository
import limdongjin.minorserver.domain.user.findByEmail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono


@Transactional
@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordGenerator: PasswordGenerator
) {
    fun getByEmail(email: String): Mono<User> {
        val user = userRepository.findByEmail(email).toMono().map {
            if(it == null) throw IllegalArgumentException("회원이 존재하지 않습니다. email: $email")
            it
        }
        return user
    }

    fun findAllByKeyword(keyword: String): List<UserResponse> {
        return userRepository.findAllByKeyword(keyword).map(::UserResponse)
    }

    suspend fun resetPassword(request: ResetPasswordRequest) {
        val user = getByEmail(request.email).awaitSingle()
        user.resetPassword(request.name, request.birthday, passwordGenerator.generate())

        withContext(Dispatchers.IO) {
            userRepository.save(user)
        }
    }

    fun editPassword(id: Long, request: EditPasswordRequest) {
        require(request.password == request.confirmPassword) { "새 비밀번호가 일치하지 않습니다." }
        userRepository.getReferenceById(id).changePassword(request.oldPassword, request.password)
    }

    fun getInformation(id: Long): UserResponse {
        return UserResponse(userRepository.getReferenceById(id))
    }
    fun getInformation(email: String): UserResponse {
        return UserResponse(userRepository.findByEmail(email)!!)
    }

}