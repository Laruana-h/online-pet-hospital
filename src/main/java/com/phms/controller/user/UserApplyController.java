package com.phms.controller.user;

import com.phms.pojo.Appointment;
import com.phms.pojo.User;
import com.phms.pojo.UserRole;
import com.phms.service.AppointmentService;
import com.phms.service.IMailService;
import com.phms.service.UserRoleService;
import com.phms.service.UserService;
import com.phms.utils.MyUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 用户预约
 */
@Controller("UserApplyController")
@RequestMapping("/user/apply")
public class UserApplyController{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private IMailService iMailService;

    /**
     * 医生管理预约页面
     * user/applyListDoctor.html
     */
    @RequestMapping("/applyListDoctor")
    public String applyListDoctor(Long petId, Model model) {
        if (petId != null) {
            model.addAttribute("petId", petId);
        }
        return "user/applyListDoctor";
    }

    /**
     * 普通用户预约页面
     * user/applyList.html
     */
    @RequestMapping("/applyList")
    public String applyList(Long petId, Model model) {
        if (petId != null) {
            model.addAttribute("petId", petId);
        }
        return "user/applyList";
    }

    /**
     * 普通用户返回查询数据渲染表格
     */
    @RequestMapping("/getAllByLimit")
    @ResponseBody
    public Object getAllByLimit(Appointment appointment) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        appointment.setUserId(user.getId());
        return appointmentService.getAllByLimit(appointment);
    }

    /**
     * 医生角色返回查询数据渲染表格
     */
    @RequestMapping("/getAllByLimitDoctor")
    @ResponseBody
    public Object getAllByLimitBaoJie(Appointment appointment) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();

        List<UserRole> list = userRoleService.getRoleByUserId(user.getId().toString());
        for( UserRole userRole:list) {
            if (new Integer(3).equals(userRole.getRoleId())) {
                appointment.setDoctorId(user.getId());
            }

        }
        return appointmentService.getAllByLimit(appointment);
    }

    /**
     * 根据id删除预约
     */
    @RequestMapping(value = "/del")
    @ResponseBody
    @Transactional
    public String delUser(Long id) {
        try {
            appointmentService.deleteById(id);
            return "SUCCESS";
        } catch (Exception e) {
            logger.error("Deletion exception!", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "ERROR";
        }
    }

    /**
     * 添加预约页面 user/applyAdd.html
     */
    @RequestMapping(value = "/add")
    public String addUserPage(Long id, Model model) {
        model.addAttribute("petId", id);
        List<User> doctors = userService.listDoctor();
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();

        model.addAttribute("phone",user.getPhone());
        model.addAttribute("address",user.getAddress());

        model.addAttribute("doctors", doctors);
        model.addAttribute("appTimes", getAppTimes());
        return "user/applyAdd";
    }

    private List<HashMap<String, Object>> getAppTimes() {
        List<HashMap<String, Object>> list = new ArrayList<>();
        list.add(AddMap("09:00 ~ 10:00", 1));
        list.add(AddMap("10:00 ~ 11:00", 1));
        list.add(AddMap("11:00 ~ 12:00", 1));
        list.add(AddMap("13:00 ~ 14:00", 1));
        list.add(AddMap("14:00 ~ 15:00", 1));
        list.add(AddMap("15:00 ~ 16:00", 1));
        list.add(AddMap("16:00 ~ 17:00", 1));
        list.add(AddMap("17:00 ~ 18:00", 1));

        list.add(AddMap("09:00 ~ 10:00", 2));
        list.add(AddMap("10:00 ~ 11:00", 2));
        list.add(AddMap("11:00 ~ 12:00", 2));
        list.add(AddMap("13:00 ~ 14:00", 2));
        list.add(AddMap("14:00 ~ 15:00", 2));
        list.add(AddMap("15:00 ~ 16:00", 2));
        list.add(AddMap("16:00 ~ 17:00", 2));
        list.add(AddMap("17:00 ~ 18:00", 2));

        list.add(AddMap("09:00 ~ 10:00", 3));
        list.add(AddMap("10:00 ~ 11:00", 3));
        list.add(AddMap("11:00 ~ 12:00", 3));
        list.add(AddMap("13:00 ~ 14:00", 3));
        list.add(AddMap("14:00 ~ 15:00", 3));
        list.add(AddMap("15:00 ~ 16:00", 3));
        list.add(AddMap("16:00 ~ 17:00", 3));
        list.add(AddMap("17:00 ~ 18:00", 3));
        return list;
    }

    private HashMap<String, Object> AddMap(String time, Integer add) {
        final String day = MyUtils.getDate2String(new Date(), add);
        time = day + "  " + time;
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", time);
        map.put("value", time);
        return map;
    }

    /**
     * 预约信息插入数据库
     */
    @RequestMapping(value = "/doAdd")
    @ResponseBody
    @Transactional
    public String doAdd(Appointment appointment) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if (appointment.getPetId() == null) {
            return "noPetId";
        }
        final List<Map<String, Object>> freeTimeByDoc = appointmentService.getFreeTimeByDoc(appointment.getDoctorId(), appointment.getOrderTime());
        if (freeTimeByDoc.size() > 0) {
            return "1";
        }

        try {
            // 当前预约人的id
            appointment.setUserId(user.getId());
            appointment.setCreateTime(new Date());
            // 状态:1申请中,2申请通过,3不通过,4已完成
            appointment.setStatus(1);
            appointmentService.add(appointment);
            User doctor = userService.selectUserByUserId(appointment.getDoctorId());
            String mailContent = "You have a new appointment! The reservation is " +user.getName()+
                    " The condition of his pet is " + appointment.getInfo()+
                    " .His mobile phone number is " +appointment.getPhone()+
                    " and the reservation time is  " +appointment.getOrderTime()+
                    " .Please arrange your time!";
            String mailTitle = "You have a new appointment";
            //发送邮件
            iMailService.sendSimpleMail(doctor.getEmail(),mailTitle,mailContent);

            return "SUCCESS";
        } catch (Exception e) {
            logger.error("Add exception!", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "ERROR";
        }
    }

    /**
     * 改变预约状态
     */
    @RequestMapping(value = "/chStatus")
    @ResponseBody
    @Transactional
    public String chStatus(Appointment appointment) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();


        try {
            appointment.setDoctorId(user.getId());
            appointmentService.update(appointment);
            // 就诊
            if (appointment.getStatus() == 4) {
                return "jz";
            }
            return "SUCCESS";
        } catch (Exception e) {
            logger.error("Add exception!", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "ERROR";
        }
    }
}
