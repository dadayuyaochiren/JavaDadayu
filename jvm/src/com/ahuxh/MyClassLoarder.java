package com.ahuxh;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * 第一周
 * 作业一
 * 自定义 Classloarder
 *
 * @author ahuxh
 */
public class MyClassLoarder extends ClassLoader {

    public static void main(String[] args) throws Exception {
        // 入参
        final String className = "Hello";
        final String methodName = "hello";
        // 自定义类加载器多态
        ClassLoader classLoarder = new MyClassLoarder();
        // 类的加载 (重写了类的 装载)
        Class<?> clazz = classLoarder.loadClass(className);
        // 反射创建对象
        Object instance = clazz.newInstance();
        // 查看 class类中的方法
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println(clazz.getSimpleName() + "." + method.getName());
            // 反射调用方法
            method.invoke(instance);
        }
        // 调用 hello方法
//        Method method = clazz.getMethod(methodName);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 路径转换, 将包名  .  ,  转为 /
        String resourcePath = name.replace(".", "/");
        //文件 后缀
        final String suffix = ".xlass";
        // 获取输入流
        // 读取字节流
        // 1.getClass().getResourceAsStream  / 开头,从 src往下取  , 否则从当前包往下去
        // 2.getClass().getClassLoader().getResourceAsStream 都从src 往下取
         try (InputStream inputStream = this.getClass().getResourceAsStream(resourcePath + suffix)) {
            // 读取数据
            int length = inputStream.available();
            // 建立缓冲区
            byte[] byteArray = new byte[length];
            // 先全部读出来
            inputStream.read(byteArray);
            // 进行编码的解密转换
            byte[] targetBytes = decode(byteArray);
            // 读取 并 操作 字节码
            // 通知后续的 操作
            return defineClass(name, targetBytes, 0, targetBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    /**
     * 解码工作
     *
     * @param byteArray
     */
    private static byte[] decode(byte[] byteArray) {
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte) (255 - byteArray[i]);
        }
        return byteArray;
    }
}
