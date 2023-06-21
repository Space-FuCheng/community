package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit); //考虑到未来分页的可能加入offset和limit

    //如果方法只有一个参数，在<if>里使用，必须加别名
    int selectDiscussPostRows(@Param("userId") int userId); //注解param取别名，以防参数过长写到sql里麻烦





}
