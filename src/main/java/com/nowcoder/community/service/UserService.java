package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    String domain;

    @Value("${server.servlet.context-path}")

    String contextPath;

    @Autowired
    LoginTicketMapper loginTicketMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        //空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不可为空");
        }

        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不可为空");
            return map; //TIP return map很关键，如果没有这句话的话，你不管存不存在都会发邮件，有了这句话，一旦出现问题就不会发送邮件了
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不可为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不可为空");
            return map;
        }
        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "账号已存在");
            return map;
        }
        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "邮箱已存在");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+ user.getSalt()) );//怪不得我登录时密码错误，没把盐和密码放一起加密而是把盐放外面去了
        user.setEmail(user.getEmail());
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/"+ user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活邮件", content);
        return map;

    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) { //不能用== string比较要用equal
//            user.setStatus(1); //能不能用setStatus，不能，因为这不修改底层。下面的才修改底层，调用sql语句的
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FALLURE;
        }
    }

    public Map<String, Object> login(String username, String password, long expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        //忘记先处理空值了
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        //检查账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "账号不存在");
            return map;
        }
        //检查状态是否激活
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "账号未激活");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) { //注意不能用不等号，因为是string 要用equal
            map.put("passwordMsg", "密码错误");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket", loginTicket.getTicket());

        return map;
    }
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);

    }

}
