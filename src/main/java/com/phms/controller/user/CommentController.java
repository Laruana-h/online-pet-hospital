package com.phms.controller.user;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.phms.mapper.TbCommentMapper;
import com.phms.pojo.TbComment;
import com.phms.pojo.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;

@Controller
@RequestMapping("/comment")
public class CommentController{
    @Autowired
    private TbCommentMapper tbCommentMapper;

    @PostMapping("/add")
    @ResponseBody
    public String add(@RequestBody TbComment comment) {
        // 判断内容是否全
        HashMap<String, Object> map = new HashMap<>();
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if (StringUtils.isEmpty(comment.getContent())) {
            //评论内容不能为空create_user_id 添加失败 400
            map.put("code", "400");
            map.put("msg", "添加失败");
            map.put("count", 0);
            map.put("data", "评论内容不能为空!");
            return JSON.toJSONString(map);
        }
        comment.setUsername(user.getName());
        comment.setCreateTime(new Date());
        comment.setCreateUserId(user.getId() + "");
        tbCommentMapper.insertSelective(comment);
        map.put("code", "200");
        map.put("msg", "添加成功");
        map.put("count", 0);
        return JSON.toJSONString(map);
    }

    @PostMapping("/remove/{id}")
    @ResponseBody
    public String remove(@PathVariable Integer id) {
        // 先删除子回复
        final int i = tbCommentMapper.deleteByPrimaryKey(id);
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", "200");
        map.put("msg", "删除成功");
        map.put("count", i);
        return JSON.toJSONString(map);
    }

}
