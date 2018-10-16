package cn.intellif.fegin.intellif.core.proxy;

import cn.intellif.fegin.intellif.annotation.MethodType;
import cn.intellif.fegin.intellif.core.RequestDefination;
import cn.intellif.fegin.intellif.core.decode.DefaultDecode;
import cn.intellif.fegin.intellif.utils.ApacheHttpUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class DefaultMethodHandler implements InvocationHandler {
    private Class clazz;

    public DefaultMethodHandler(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
       RequestDefination requestDefination =  DefaultDecode.decodeMethod(method,clazz);
        String url = requestDefination.getUrl();
        //发送http请求
        if(requestDefination.getMethodType()== MethodType.GET){
           String param = createGetParam(requestDefination.getBody(),args);
           if(param!=null){
               url+=param;
           }
           Object result =  ApacheHttpUtils.get(url,null,null);
           return result;
        }else if(requestDefination.getMethodType()==MethodType.POST){
            return ApacheHttpUtils.postForm(url,createPostParam(requestDefination.getBody(),args),null);
        }
       return null;
    }

    private Map<String,Object> createPostParam(String[] body,Object[] args){
        if(body==null||body.length==0)
            return null;
        if(body.length!=args.length){
            throw new RuntimeException("参数长度和RequestParam不一致");
        }
        Map<String,Object> paramMap = new HashMap<>();
        for(int i=0;i<body.length;i++){
            paramMap.put(body[i],args[i]);
        }
        return paramMap;
    }

    private String createGetParam(String[] body, Object[] args){
        if(body==null||body.length==0)
            return null;
        if(body.length!=args.length){
            throw new RuntimeException("参数长度和RequestParam不一致");
        }
        StringBuilder sb = new StringBuilder("?");
        for(int i=0;i<body.length;i++){
            sb.append(body[i]).append("=").append(args[i]);
            if(i!=body.length-1){
                sb.append("&");
            }
        }
        return sb.toString();
    }
}
