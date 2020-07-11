package com.github.mnadeem.wishing.service;

import javax.mail.MessagingException;

import com.github.mnadeem.wishing.service.data.Mail;

/**
 * 
 * @author Mohammad Nadeem (coolmind182006@gmail.com)
 *
 */
public interface EmailService {
	
	void send(Mail mail) throws MessagingException;
}
