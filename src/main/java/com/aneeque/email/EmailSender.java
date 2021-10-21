package com.aneeque.email;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

;

@Service
public class EmailSender {

    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendMail(String email, String to){
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
        try {
            messageHelper.setSubject("Confirm your account");
            messageHelper.setFrom("no-reply@aneeque.com");
            messageHelper.setText(email, true);
            messageHelper.setTo(to);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
