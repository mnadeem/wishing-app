package com.github.mnadeem.wishing.service;

import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.github.mnadeem.wishing.service.data.Mail;

@Service
public class DefaultEmailService implements EmailService {

	private static final String HEADER_EXPIRY_DATE = "Expiry-Date";
	private static final String HEADER_EXPIRES = "Expires";

	private static Logger logger = LoggerFactory.getLogger(DefaultEmailService.class);

    @Autowired
    private JavaMailSender emailSender;
	@Autowired
	private ResourceLoader resourceLoader;

	@Override
	@Async
    public void send(Mail mail) throws MessagingException {
		if (logger.isTraceEnabled()) {
			logger.trace("Sending mail for {}", mail);
		}
        MimeMessage message = buildMessage(mail);
        emailSender.send(message);
		if (logger.isTraceEnabled()) {
			logger.trace("mail sent for {}", mail);
		}		
    }

	private MimeMessage buildMessage(Mail mail) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
        
        fillMessage(mail, message);
        addHeaders(mail, message);
        
		return message;
	}

	private void fillMessage(Mail mail, MimeMessage message) throws MessagingException {
		MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name());

        helper.addAttachment("logo", resourceLoader.getResource(mail.getImage()));
        String inlineImage = "<img src=\"cid:logo\" width=\"100%\" style=\"max-width: 80vw; margin: 0; border: 0; padding: 0; display: block;\"></img><br/>";

        helper.setText(inlineImage + mail.getContent(), true);
        helper.setSubject(mail.getSubject());
        helper.setTo(mail.getTo());
        helper.setFrom(mail.getFrom());
        
        if (mail.getCc() != null) {			
        	helper.setCc(mail.getCc());
		}        
	}

	private void addHeaders(Mail mail, MimeMessage message) {
		if (mail.hasExpire()) {
			try {
				message.setHeader(HEADER_EXPIRES, mail.getExpire());
				message.setHeader(HEADER_EXPIRY_DATE, mail.getExpire()); // Outlook works with this, it seems
			} catch (MessagingException e) {
				logger.error("Error setting expire ", e);
			}
		}		
	}
}