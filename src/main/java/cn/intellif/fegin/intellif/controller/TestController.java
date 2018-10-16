package cn.intellif.fegin.intellif.controller;

import cn.intellif.fegin.intellif.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private ITestService testService;

    @RequestMapping("/test1")
    public Object test1(@RequestParam("name")String name){
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>name:"+name);
        return "success";
    }

    @RequestMapping("/test2")
    public Object test2(){
        return testService.test1("yucui");
    }
}
