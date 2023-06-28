package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.xml.transform.Templates;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;
    @Test
    public void SendTests() {
        mailClient.sendMail("wz840332892@163.com","Tests", "sjs");
    }

    @Test
    public void SendHtmlTests() {
        Context context = new Context();
        context.setVariable("username", "xixi");
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("wz840332892@163.com", "htmlTest", content);
    }
}
