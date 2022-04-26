package com.phms.controller.open;


import com.phms.model.GitHubConstant;
import com.phms.model.ResultMap;
import com.phms.pojo.Page;
import com.phms.pojo.User;
import com.phms.service.PageService;
import com.phms.service.UserRoleService;
import com.phms.service.UserService;
import com.phms.utils.HttpClientUtils;
import com.phms.utils.MD5;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 登录控制类
 */
@Controller("OpenLogin")
@RequestMapping()
public class LoginController{
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private ResultMap resultMap;
    @Autowired
    private UserService userService;
    @Autowired
    private PageService pageService;
    @Autowired
    private UserRoleService userRoleService;

    /**
     * 返回 尚未登陆信息
     */
    @RequestMapping(value = "/notLogin", method = RequestMethod.GET)
    @ResponseBody
    public ResultMap notLogin() {
        logger.warn("Not yet logged in!");
        return resultMap.success().message("You are not logged in!");
    }

    /**
     * 返回 没有权限
     */
    @RequestMapping(value = "/notRole", method = RequestMethod.GET)
    @ResponseBody
    public ResultMap notRole() {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if (user != null) {
            logger.info("{}---No privilege！", user.getName());
        }
        return resultMap.success().message("You don't have priveilege！");
    }


/**演示页面**/
    /**
     * Method name: logout <BR>
     * Description: 退出登录 <BR>
     *
     * @return String<BR>
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if (null != user) {
            logger.info("{}---Log out！", user.getName());
        }
        subject.logout();
        return "login";
    }

    /**
     * Method name: login <BR>
     * Description: 登录验证 <BR>
     * Remark: <BR>
     *
     * @param username 用户名
     * @param password 密码
     * @return ResultMap<BR>
     */
    @RequestMapping(value = "/login")
    @ResponseBody
    public ResultMap login(String username, String password) {
        return userService.login(username, password);
    }

    @RequestMapping("/callback")
    public String callback(String code, String state, Model model, HttpServletRequest req) throws Exception{

        if(!StringUtils.isEmpty(code)&&!StringUtils.isEmpty(state)){
            //拿到我们的code,去请求token
            //发送一个请求到
            String token_url = GitHubConstant.TOKEN_URL.replace("CODE", code);
            //得到的responseStr是一个字符串需要将它解析放到map中
            String responseStr = HttpClientUtils.doGet(token_url);
            // 调用方法从map中获得返回的--》 令牌
            String token = HttpClientUtils.getMap(responseStr).get("access_token");

            //根据token发送请求获取登录人的信息  ，通过令牌去获得用户信息
//            String userinfo_url = GitHubConstant.USER_INFO_URL.replace("TOKEN", token);
            responseStr = HttpClientUtils.doGetWithHeader(GitHubConstant.USER_INFO_URL,token);//json

            Map<String, String> responseMap = HttpClientUtils.getMapByJson(responseStr);
            User user = new User();
            user.setGithubId(responseMap.get("id"));
            User u = userService.getByGithubId(responseMap.get("id"));
            if (u==null) {
                user.setName(responseMap.get("name"));
                user.setPhone(user.getGithubId());
                user.setPassword(MD5.md5("123456"));
                user.setCreateTime(new Date());
                userService.save(user);
                String[] ids = new String[1];
                ids[0] = user.getId() + "";
                // 普通用户
                userRoleService.addUserRole(2, ids);
                userService.githubLogin(user.getName(),user.getPassword());
            } else {
                userService.githubLogin(u.getName(),u.getPassword());
            }
            // 成功则登陆
            Subject subject = SecurityUtils.getSubject();
            User user1 = (User) subject.getPrincipal();
            if (null != user1) {
                model.addAttribute("user", user1);
            } else {
                return "login";
            }

            List<Page> pageList = pageService.getAllRolePageByUserId(user1.getId() + "");
            model.addAttribute("pageList", pageList);
            return "index";
        }
        // 否则返回到登陆页面
        return "login";
    }




    /**
     * Method name: login <BR>
     * Description: 登录页面 <BR>
     *
     * @return String login.html<BR>
     */
    @RequestMapping(value = "/index")
    public String login() {
        return "login";
    }

    /**
     * 注册页面 regist.html
     */
    @RequestMapping(value = "/regist")
    public String regist() {
        return "regist";
    }

    /**
     * 注册
     */
    @RequestMapping(value = "/doRegist")
    @ResponseBody
    public ResultMap doRegist(User user) {
        System.out.println(user);
        User u = userService.getUserByPhoneAndName(user.getPhone(), null);
        if (u != null) {
            return resultMap.success().message("This phone number is already registered!");
        }
        try {
            user.setPassword(MD5.md5(user.getPassword()));
            user.setCreateTime(new Date());
            userService.save(user);
            String[] ids = new String[1];
            ids[0] = user.getId() + "";
            // 普通用户
            userRoleService.addUserRole(2, ids);
            return resultMap.success().message("Register successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return resultMap.fail().message("Register failed!");
        }
    }

    /**
     * Method name: index <BR>
     * Description: 登录页面 <BR>
     *
     * @return String login.html<BR>
     */
    @RequestMapping(value = "/")
    public String index(Model model) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();

        if (null != user) {
            model.addAttribute("user", user);

            List<Page> pageList = pageService.getAllRolePageByUserId(user.getId() + "");

            model.addAttribute("pageList", pageList);
            return "index";
        } else {
            return "login";
        }
    }

    @RequestMapping(value = "/sy")
    public String sy(Model model) {
        return "sy";
    }

    /**
     * Method name: main <BR>
     * Description: 进入主页面 <BR>
     * index.html
     */
    @RequestMapping(value = "/main")
    public String main(Model model) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if (null != user) {
            model.addAttribute("user", user);
        } else {
            return "login";
        }

        List<Page> pageList = pageService.getAllRolePageByUserId(user.getId() + "");
        model.addAttribute("pageList", pageList);
        return "index";
    }

    /**
     * Method name: checkUserPassword <BR>
     * Description: 检测旧密码是否正确 <BR>
     *
     * @param password 旧密码
     * @return boolean 是否正确<BR>
     */
    @RequestMapping(value = "/user/checkUserPassword")
    @ResponseBody
    public boolean checkUserPassword(String password) {
        return userService.checkUserPassword(password);
    }

    /**
     * Method name: updatePassword <BR>
     * Description: 更新密码 <BR>
     *
     * @param password 旧密码
     * @return String 是否成功<BR>
     */
    @RequestMapping(value = "/user/updatePassword")
    @ResponseBody
    public String updatePassword(String password) {
        return userService.updatePassword(password);
    }
}
