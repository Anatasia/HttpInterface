package com.netease.anatasia.shfileboot.controller;

import com.netease.anatasia.shfileboot.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
/**
 * Created by xujuan1 on 2017/7/3.
 */
@Controller
@RequestMapping("/project")
public class ShFileBootController {

    @Resource
    private TaskService taskService;
    @RequestMapping("/module")
    public String getTaskInfo(HttpServletRequest request,Model model){
        String proId = request.getParameter("proId");
        String taskInfo = taskService.getTaskInfo(proId);
        model.addAttribute("taskInfo",taskInfo);
        return "task";
    }

}
