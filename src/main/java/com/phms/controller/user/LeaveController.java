package com.phms.controller.user;

import com.phms.mapper.TbCommentMapper;
import com.phms.mapper.UserRoleMapper;
import com.phms.pojo.TbComment;
import com.phms.pojo.User;
import com.phms.pojo.UserRole;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author liuyanlong
 * @Description 评论留言
 * @Date 2022/3/15 16:08
 **/
@Controller("LeaveController")
public class LeaveController{
    @Autowired
    private TbCommentMapper tbCommentMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @RequestMapping("/leave")
    public String leave(Model model) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        final UserRole role = userRoleMapper.getRoleId(user.getId() + "");
        final Integer roleId = role.getRoleId();
        model.addAttribute("user", user);
        model.addAttribute("userId", user.getId().toString());
        model.addAttribute("roleId", roleId);
        List<TbComment> list = null;
        if (roleId == 2) {
            list = tbCommentMapper.selectParent(user.getId());
        } else {
            list = tbCommentMapper.selectParent(null);
        }
        // 第一层
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                // 递归查询下层
                getList(list.get(i));
            }
        }
        model.addAttribute("clist", list);
        return "leave";
    }

    // 递归查询下级评论
    private void getList(TbComment comment) {
        TbComment commentDto = new TbComment();
        commentDto.setParentId(comment.getId());
        List<TbComment> comments = tbCommentMapper.selectByAll(commentDto);
        // 判断comments是否为空
        if (comments.size() > 0) {
            comment.setCommentList(comments);
            for (int i = 0; i < comments.size(); i++) {
                // 递归查询下层
                getList(comments.get(i));
            }
        }

    }

}
