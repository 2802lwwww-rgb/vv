package com.red.education.common.utils;

import org.springframework.stereotype.Component;

/**
 * 邮件工具类（控制台模式）
 * 验证码将直接输出到控制台，无需配置邮件服务
 */
@Component
public class EmailUtils {

    /**
     * 发送简单邮件（控制台输出模式）
     */
    public void sendSimpleMail(String to, String subject, String content) {
        System.out.println("=== 邮件发送（控制台模式） ===");
        System.out.println("收件人: " + to);
        System.out.println("主题: " + subject);
        System.out.println("内容: " + content);
        System.out.println("============================");
    }

    /**
     * 发送验证码邮件（控制台输出模式）
     */
    public void sendVerificationCode(String to, String code) {
        String subject = "【红色教育平台】密码重置验证码";
        String content = String.format(
                "您好，\n\n" +
                        "您正在重置密码，验证码为：%s\n\n" +
                        "验证码5分钟内有效，请勿泄露给他人。\n\n" +
                        "如非本人操作，请忽略此邮件。\n\n" +
                        "红色教育平台",
                code);
        sendSimpleMail(to, subject, content);
    }
}
