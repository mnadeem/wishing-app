package com.github.mnadeem.wishing.service.data;

import java.util.Arrays;

import org.springframework.util.StringUtils;

/**
 * 
 * @author Mohammad Nadeem (coolmind182006@gmail.com)
 *
 */
public class Mail {

    private String from;
    private String to;
    private String subject;
    private String content;
    private String image;
    private String[] cc;
    private String expire;

    public Mail() {
    	
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String[] getCc() {
		return cc;
	}
	
	public boolean hasExpire() {
		return StringUtils.hasText(expire);
	}

	public String getExpire() {
		return expire;
	}

	public void setExpire(String expire) {
		this.expire = expire;
	}

	public void setCc(String cc) {
		if (StringUtils.hasText(cc)) {
			String[] result = cc.split(",");
			if (result !=null && result.length > 0) {
				this.cc = result;
			}
		}
	}

	@Override
	public String toString() {
		return "Mail [from=" + from + ", to=" + to + ", subject=" + subject + ", content=" + content + ", image="
				+ image + ", cc=" + Arrays.toString(cc) + "]";
	}
}