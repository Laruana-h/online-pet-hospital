package com.phms.controller.user;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.phms.pojo.Order;
import com.phms.pojo.User;
import com.phms.pojo.UserRole;
import com.phms.service.IOrderService;
import com.phms.service.RoleService;
import com.phms.service.UserRoleService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 * 【请填写功能名称】Controller
 * 
 * @author ruoyi
 * @date 2022-04-22
 */

@RequestMapping("/user/order")
@Controller
public class OrderController
{
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;

    /**
     * 查询【请填写功能名称】列表
     */

    @PostMapping("/list")
    @ResponseBody
    public Object list(Order order)

    {

        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        List<UserRole> roles = userRoleService.getRoleByUserId(user.getId().toString());
        Boolean isAdmin = false;
        for (UserRole userRole:roles) {
            if (userRole.getRoleId().equals(new Integer(1))){
                isAdmin = true;
                break;
            }
        }
        if (!isAdmin) {
            order.setUserId(user.getId());
        }
        return orderService.selectOrderList(order);

    }

    @RequestMapping("/order")
    public String xq(Model model) {
        return "/user/order/order";
    }
    @RequestMapping("/myOrder")
    public String xq1(Model model) {
        return "/user/order/myorder";
    }
    /**
     * 修改【请填写功能名称】
     */
    @PostMapping("/pickUp")
    @ResponseBody
    public Object edit(@RequestBody Order order)
    {
        orderService.updateOrder(order);
        return "SUCCESS";
    }

}
