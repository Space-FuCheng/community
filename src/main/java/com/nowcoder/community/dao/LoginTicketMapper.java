package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {

    @Insert({
            "insert into login_ticket(user_id, ticket, status, expired) ", //最好加个空格，这样sql语句拼起来有个空格不会出错
            "values(#{userId}, #{ticket}, #{status}, #{expired}) "
    })
    @Options(useGeneratedKeys = true, keyProperty = "id") //设置主键自动生成，并赋给id
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket ",
            "where ticket = #{ticket} "
    })
    LoginTicket selectByTicket(String ticket);

    @Update({
            "<script>",
            "update login_ticket set status = #{status} ",
            "where ticket = #{ticket} ",
            "<if test=\"ticket!=null\">", //少了个了>，我说怎么报错了
            "and 1=1",
            "</if>",
            "</script>"
    })
    //注解里也可以写if，需要像上面一样，放在<script>里面，注意还有转义
    int updateStatus(String ticket, int status);
}
