package net.f0error.bootwebapp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = {"net.f0error.bootwebapp.service"})
@EnableAspectJAutoProxy
public class AoPConfig {
}
