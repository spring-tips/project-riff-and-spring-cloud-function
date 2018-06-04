package com.example.mailer;

import com.sendgrid.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import java.util.function.Function;

@SpringBootApplication
public class MailerApplication {

		private final Log logger = LogFactory.getLog(getClass());

		@Bean
		SendGrid sendGrid(@Value("${SENDGRID_API_KEY}") String key) {
				return new SendGrid(key);
		}

		@Bean
		Function<String, Boolean> mail(SendGrid mailer) {
				return email -> run(mailer, email);
		}

//		@Bean
		ApplicationRunner run(SendGridAPI mailer) {
				return a -> run(mailer, "josh@joshlong.com");
		}

		private boolean run(SendGridAPI mailer, String destinationAddress) {
				try {
						Email from = new Email("spring-tips@joshlong.com");
						String subject = "Bootiful Riff";
						Email to = new Email(destinationAddress);
						Content content = new Content("text/plain", "Hello world!");
						Mail mail = new Mail(from, subject, to, content);
						Request request = new Request();
						request.setMethod(Method.POST);
						request.setEndpoint("mail/send");
						request.setBody(mail.build());
						Response response = mailer.api(request);
						logger.info(response.getStatusCode());
						logger.info(response.getBody());
						logger.info(response.getHeaders());
						return response.getStatusCode() == HttpStatus.OK.value();
				}
				catch (Exception e) {
						throw new RuntimeException(e);
				}
		}

		public static void main(String[] args) {
				SpringApplication.run(MailerApplication.class, args);
		}
}
