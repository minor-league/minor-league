package limdongjin.minorserver.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private var username: String,
    private var password: String,
    private var accountNonExpired: Boolean,
    private var accountNonLocked: Boolean,
    private var credentialsNonExpired: Boolean,
    private var enabled: Boolean
): UserDetails {
    private val permissions = ArrayList<String>()

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities = ArrayList<GrantedAuthority>()
        permissions.forEach { permission ->
            authorities.add(SimpleGrantedAuthority (permission));
        }

        return authorities;
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    fun setPassword(newPassword: String) {
        this.password = newPassword
    }

    fun setUsername(newUserName: String){
        this.username = newUserName
    }

    override fun isAccountNonExpired(): Boolean {
        return accountNonExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return accountNonLocked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return credentialsNonExpired
    }

    override fun isEnabled(): Boolean {
        return enabled
    }
}