package com.github.mnadeem.wishing;

import java.time.LocalDate;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.mnadeem.wishing.service.DefaultEmailService;
import com.github.mnadeem.wishing.service.WishingDataService;
import com.github.mnadeem.wishing.service.data.Mail;
import com.github.mnadeem.wishing.service.data.Wish;

@Component
public class WishingJob {

	private static Logger logger = LoggerFactory.getLogger(WishingJob.class);

	@Autowired
	private WishingDataService dataService;
	@Autowired
	private DefaultEmailService emailService;
	@Autowired
	private Environment env;

	@Scheduled(cron = "${app.schedule.corn}")
	public void job() throws Exception {
		LocalDate now = LocalDate.now();
		logger.debug("Job running for {}", now);
		dataService.forEach(now, this::sendEmail);
		logger.debug("Job finished for {}", now);
	}

	private void sendEmail(Wish wish) {
		if (wish.shouldWish() && isEnabled(wish)) {

			try {
				if (logger.isTraceEnabled()) {
					logger.trace("Sending mail for {}", wish);
				}
				this.emailService.send(buildMail(wish));
				if (logger.isTraceEnabled()) {
					logger.trace("mail sent for {}", wish);
				}
				
			} catch (MessagingException e) {
				logger.error("Error Sending message", e);
			}

		} else {
			if (logger.isTraceEnabled()) {
				logger.debug("Wish not applicable / enabled for {}", wish);
			}
		}
	}

	private boolean isEnabled(Wish wish) {
		StringBuilder key = new StringBuilder();
		key.append("app.mailer.");
		if (wish.isBirthday()) {
			key.append("birthday");
		} else {
			key.append("anniversary");
		}
		key.append(".enabled");
		return env.<Boolean>getProperty(key.toString(), Boolean.class, Boolean.TRUE);
	}

	private Mail buildMail(Wish wish) {
		String from = env.<String>getProperty("app.name" + wish.getPartition() + ".from", String.class, "donotreply@nowhere.com");
		String cc = env.<String>getProperty("app.name" + wish.getPartition() + ".cc", String.class, "");
		Mail mail = new Mail();
		mail.setTo(wish.getEmail());
		mail.setFrom(from);
		mail.setSubject(buildSubject(wish));
		mail.setContent(buildContent(wish));
		mail.setImage(buildImage(wish));
		mail.setCc(cc);
		
		if (logger.isTraceEnabled()) {
			logger.trace("{}", mail);
		}
		
		return mail;
	}

	private String buildImage(Wish wish) {
		String image = null;
		if (wish.isBirthday()) {
			image = getBirthDayImageName();
		} else {
			image = getAnniversaryImageName(wish);
		}
		return image;
	}

	private String getAnniversaryImageName(Wish wish) {
		int anniversarycount = env.<Integer>getProperty("app.anniversary.years_count", Integer.class, 1);
		int anniversary = wish.getYears();

		int maxImagesCount = getImagesCount(anniversarycount, anniversary);

		StringBuilder builder = new StringBuilder();
		builder.append("data/images/anniversay/");
		if (anniversary > anniversarycount) {
			builder.append("default/");
		} else {
			builder.append(anniversary).append("/");
		}
		builder.append(randomNumber(maxImagesCount)).append(".png");
		return builder.toString();
	}

	private int getImagesCount(int anniversarycount, int anniversary) {
		String key = "app.anniversary.year" + anniversary + ".image_count";
		if (anniversary > anniversarycount) {
			key = "app.anniversary.default.image_count";
		}
		int count = env.<Integer>getProperty(key, Integer.class, 1);
		return count;
	}

	private String getBirthDayImageName() {
		int count = env.<Integer>getProperty("app.birthday.image_count", Integer.class, 1);
		return "data/images/birthday/" + randomNumber(count) + ".png";
	}

	private int randomNumber(int max) {
		int min = 1;
		return (int) ((Math.random() * (max - min)) + min);
	}

	private String buildContent(Wish wish) {
		return "";
	}

	private String buildSubject(Wish wish) {
		StringBuilder subject = new StringBuilder();
		subject.append(wish.getWish()).append(wish.getName());
		if (wish.isBirthday()) {
			subject.append("!");
		} else {
			subject.append(", ").append(wish.getYearsMessage()).append(" Completed!");
		}
		
		return subject.toString();
	}
}
