package cn.intellif.fegin.intellif.core.decode;

import cn.intellif.fegin.intellif.annotation.FeginClient;
import cn.intellif.fegin.intellif.annotation.RequestMapping;
import cn.intellif.fegin.intellif.annotation.RequestParam;
import cn.intellif.fegin.intellif.core.RequestDefination;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class DefaultDecode {
   public static RequestDefination decodeMethod(Method method,Class clazz){
       FeginClient feginClient = (FeginClient) clazz.getAnnotation(FeginClient.class);
       RequestDefination defination = new RequestDefination();
       String url_pre = feginClient.provider();
       RequestMapping requestMapping =  method.getAnnotation(RequestMapping.class);
       String url_end = requestMapping.value();
       int methodType = requestMapping.methodType();
       defination.setMethodType(methodType);
       defination.setUrl(url_pre+"/"+url_end);
      Annotation[][] parameterTypes = method.getParameterAnnotations();
       if(parameterTypes!=null&&parameterTypes.length>0){
           int size = parameterTypes.length;
           String[] body = new String[size];
           for(int i=0;i<size;i++){
               body[i]=((RequestParam)(parameterTypes[i][0])).value();
           }
           defination.setBody(body);
       }
       return defination;
   }
}
