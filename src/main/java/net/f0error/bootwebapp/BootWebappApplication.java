package net.f0error.bootwebapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@SpringBootApplication
public class BootWebappApplication {



	public static void main(String[] args) {
		SpringApplication.run(BootWebappApplication.class, args);
	}
}
