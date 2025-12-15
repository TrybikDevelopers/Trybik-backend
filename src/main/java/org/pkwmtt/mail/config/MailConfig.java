package org.pkwmtt.mail.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Objects;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfig {
    
    private final Environment environment;
    
    private String username;
    private String password;
    private String host;
    private int port;
    
    @PostConstruct
    private void assignAndValidateProperties () {
        username = environment.getProperty("spring.mail.username");
        password = environment.getProperty("spring.mail.password");
        host = environment.getProperty("spring.mail.host");
        port = Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.mail.port")));
    }
    
    @Bean
    public JavaMailSender javaMailSender () {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", environment.getProperty("spring.mail.protocol", "smtp"));
        // Respect properties from configuration (allows tests to disable STARTTLS for GreenMail)
        props.put("mail.smtp.auth", environment.getProperty("spring.mail.properties.mail.smtp.auth", "false"));
        props.put("mail.smtp.starttls.enable", environment.getProperty("spring.mail.properties.mail.smtp.starttls.enable", "false"));
        props.put("mail.smtp.ssl.enable", environment.getProperty("spring.mail.properties.mail.smtp.ssl.enable", "false"));
        
        return mailSender;
    }
    
}
