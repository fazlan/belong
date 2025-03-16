package au.com.belong.customerphoneapi.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Customer Phone Number Management API",
        version = "1.0",
        description = "Reactive API for managing customer phone numbers in the system"
))
public class OpenAPIConfiguration {
}