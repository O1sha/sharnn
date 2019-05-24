

package com.sai.lendperfect;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.MessageSourceAutoConfiguration;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages={"com.sai.lendperfect"},exclude = MessageSourceAutoConfiguration.class)
public class Lendperfect extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Lendperfect.class);
    }
    
    public static void main(String[] args) {
		SpringApplication.run(Lendperfect.class, args);
	}

}
