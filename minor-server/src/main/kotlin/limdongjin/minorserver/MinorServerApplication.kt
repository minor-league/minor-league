package limdongjin.minorserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class MinorServerApplication

fun main(args: Array<String>) {
    runApplication<MinorServerApplication>(*args)
}
