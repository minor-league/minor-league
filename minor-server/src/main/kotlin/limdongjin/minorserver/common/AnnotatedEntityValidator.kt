package limdongjin.minorserver.api

import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class AnnotatedEntityValidator(val validator: Validator){
    fun validate(target: Any): Errors? {
        val errors: Errors = BeanPropertyBindingResult(target, target::class.simpleName!!)
        validator.validate(target, errors)
        return errors.takeIf { it.hasErrors() } ?: return null
    }
}