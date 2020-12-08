package com.mayikt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName IndexController
 * @Author 蚂蚁课堂余胜军 QQ644064779 www.mayikt.com
 * @Version V1.0
 **/
@Controller
public class IndexController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @ResponseBody
    @RequestMapping("/addMember")
    public String addMember() {
        return "新增用户";
    }

    @ResponseBody
    @RequestMapping("/delMember")
    public String delMember() {
        return "删除用户";
    }

    @ResponseBody
    @RequestMapping("/updateMember")
    public String updateMember() {
        return "修改用户";
    }

    @ResponseBody
    @RequestMapping("/showMember")
    public String showMember() {
        return "查询用户";
    }

    @ResponseBody
    @RequestMapping("/successAbcLogin")
    public String successAbcLogin() {
        return "登录成功跳转";
    }

    @ResponseBody
    @RequestMapping("/showMemberNo")
    public String showMemberNo() {
        return "查询用户No";
    }



    @RequestMapping("/login")
    public String login() {
        return "login";
    }
}
