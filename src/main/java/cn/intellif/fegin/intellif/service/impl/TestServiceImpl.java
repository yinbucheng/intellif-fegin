package cn.intellif.fegin.intellif.service.impl;

import cn.intellif.fegin.intellif.service.ITestService;

public class TestServiceImpl implements ITestService {
    @Override
    public String test1(String name) {
        return "fail";
    }
}
