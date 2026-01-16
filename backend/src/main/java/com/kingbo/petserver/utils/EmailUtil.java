package com.kingbo.petserver.utils;

import com.kingbo.petserver.exception.PethomeException;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 邮件发送工具类
 */
@Component
public class EmailUtil {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;


    /**
     * 发送简单文本邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    public void sendSimpleMail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender); // 发件人
            message.setTo(to); // 收件人
            message.setSubject(subject); // 邮件标题
            message.setText(content); // 邮件内容
            mailSender.send(message);
        } catch (Exception e) {
            PethomeException.throwPetException("邮件发送失败");
        }
    }

    /**
     * 发送简单文本邮件(支持多个收件人)
     *
     * @param toArray 收件人数组
     * @param subject 主题
     * @param content 内容
     */
    public void sendSimpleMail(String[] toArray, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(toArray);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    /**
     * 发送HTML邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content HTML内容
     */
    public void sendHtmlMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true表示支持HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            PethomeException.throwPetException("邮件发送失败");
        }
    }

    /**
     * 发送带附件的邮件
     *
     * @param to       收件人
     * @param subject  主题
     * @param content  内容
     * @param filePath 附件路径
     */
    public void sendAttachmentMail(String to, String subject, String content, String filePath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            // 添加附件
            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = file.getFilename();
            helper.addAttachment(fileName, file);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    /**
     * 发送带多个附件的邮件
     *
     * @param to        收件人
     * @param subject   主题
     * @param content   内容
     * @param filePaths 附件路径数组
     */
    public void sendAttachmentMail(String to, String subject, String content, String[] filePaths) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            // 添加多个附件
            for (String filePath : filePaths) {
                FileSystemResource file = new FileSystemResource(new File(filePath));
                String fileName = file.getFilename();
                helper.addAttachment(fileName, file);
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("邮件发送失败", e);
        }
    }


    /**
     * 发送带抄送和密送的邮件
     *
     * @param to      收件人 主要汇报人/主要收件人
     * @param cc      抄送人 要让这些人知会一下
     * @param bcc     密送人 悄悄告诉这些人 收件人和抄送人都看不到密送人
     * @param subject 主题
     * @param content 内容
     */
    public void sendMailWithCcAndBcc(String to, String cc, String bcc,
                                     String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(sender);
            helper.setTo(to);
            if (cc != null && !cc.isEmpty()) {
                helper.setCc(cc);
            }
            if (bcc != null && !bcc.isEmpty()) {
                helper.setBcc(bcc);
            }
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("邮件发送失败", e);
        }
    }
}