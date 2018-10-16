package cn.intellif.fegin.intellif.core;


import cn.intellif.fegin.intellif.core.proxy.DefaultMethodHandler;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class FeginInvokeFactoryBean<T> implements FactoryBean<T> {
    private Class<T> clazz;
    private Class fallback;

    public Class<T> getClazz() {
        return clazz;
    }

    public Class getFallback() {
        return fallback;
    }

    public void setFallback(Class fallback) {
        this.fallback = fallback;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }


    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[]{clazz},new DefaultMethodHandler(clazz));
    }


    @Override
    public Class<?> getObjectType() {
        return clazz;
    }
}
