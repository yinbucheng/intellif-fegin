package cn.intellif.fegin.intellif.service;

import cn.intellif.fegin.intellif.annotation.FeginClient;
import cn.intellif.fegin.intellif.annotation.MethodType;
import cn.intellif.fegin.intellif.annotation.RequestMapping;
import cn.intellif.fegin.intellif.annotation.RequestParam;
import cn.intellif.fegin.intellif.service.impl.TestServiceImpl;

@FeginClient(provider = "http://127.0.0.1:9090",fallback = TestServiceImpl.class)
public interface ITestService {

    @RequestMapping(value="/test/test1",methodType = MethodType.POST)
    String test1(@RequestParam("name") String name);
}
