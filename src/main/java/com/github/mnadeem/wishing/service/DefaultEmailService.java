package com.github.mnadeem.wishing.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.github.mnadeem.wishing.service.data.Mail;

@Service
public class DefaultEmailService implements EmailService {

    @Autowired
    private JavaMailSender emailSender;
	@Autowired
	private ResourceLoader resourceLoader;

	@Override
    public void send(Mail mail) throws MessagingException {

        MimeMessage message = buildMessage(mail);
        emailSender.send(message);
    }

	private MimeMessage buildMessage(Mail mail) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
        
        fillMessage(mail, message);
        
		return message;
	}

	private void fillMessage(Mail mail, MimeMessage message) throws MessagingException {
		MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name());

        helper.addAttachment("logo.png", resourceLoader.getResource("classpath:" + mail.getImage()));
        String inlineImage = "<img src=\"cid:logo.png\"></img><br/>";

        helper.setText(inlineImage + mail.getContent(), true);
        helper.setSubject(mail.getSubject() + LocalDateTime.now());
        helper.setTo(mail.getTo());
        helper.setFrom(mail.getFrom());
        if (mail.getCc() != null) {			
        	helper.setCc(mail.getCc());
		}
	}
}