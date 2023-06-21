package com.nowcoder.community.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello haha";
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumberation = request.getHeaderNames();
        while (enumberation.hasMoreElements()) {
            String name = enumberation.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":" + value);
        }
        System.out.println(request.getParameter("code"));

        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write("<h1>niukewang<h1>");
        writer.close();
    }

    //get请求
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "20")int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "students";
    }

    //student/1
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a";
    }

    //POST
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody //不加这个注解默认返回html
    public String saveStudent(String name, int age) {
        System.out.println(name + " :" + age);
        return "success";
    }

    //相应html
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age","20");
        mav.setViewName("/demo/view"); //默认是html
        return mav;
    }

    //响应
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name","浙大");
        model.addAttribute("age","99");
        return "/demo/view";
    }

    //响应JSON数据(异步请求)
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name","你好");
        emp.put("age",23);
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name","你好");
        emp.put("age",23);
        Map<String, Object> emp2 = new HashMap<>();
        emp2.put("name","你好a a ");
        emp2.put("age",223);

        list.add(emp);
        list.add(emp2);
        return list;
    }


}
