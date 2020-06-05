package com.github.mnadeem.wishing.service.data;

import org.springframework.util.StringUtils;

public class Mail {

    private String from;
    private String to;
    private String subject;
    private String content;
    private String image;
    private String[] cc;

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
				+ image + "]";
	}	
}