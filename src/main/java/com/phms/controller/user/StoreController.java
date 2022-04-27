package com.phms.controller.user;

import com.phms.pojo.Notice;
import com.phms.utils.MyUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author Sijie He
 * @Date 2022/4/21 15:41
 * @Version 1.0
 */
@Controller("StoreController")
@RequestMapping("/store")
public class StoreController {
    @RequestMapping("/userStore")
    public String xq(Model model) {
        return "/user/store/store";
    }
}
