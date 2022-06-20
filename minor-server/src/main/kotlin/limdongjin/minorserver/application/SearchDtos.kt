package limdongjin.minorserver.application

import javax.validation.constraints.NotBlank


data class SearchRequestDto(
    @field:NotBlank
    val query: String
)