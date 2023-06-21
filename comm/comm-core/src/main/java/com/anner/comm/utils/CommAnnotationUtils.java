package com.anner.comm.utils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.anner.comm.annotations.CommService;
import com.anner.comm.info.CommServiceInfo;

public class CommAnnotationUtils {

     private CommAnnotationUtils() {
     }

     public static CommServiceInfo getServicesInfo(Class<?> cls) {
          CommService anno = cls.getAnnotation(CommService.class);
          if (anno == null) {
               for (Class<?> clazz : cls.getInterfaces()) {
                    anno = clazz.getAnnotation(CommService.class);
                    if (anno != null) {
                         cls = clazz;
                         break;
                    }
               }
          }

          if (anno == null) {
               throw new UnsupportedOperationException("@CommService not found");
          }
          checkMethods(cls.getMethods());
          return new CommServiceInfo(anno.value(), cls, cls.getMethods());
     }

     private static void checkMethods(Method[] methods) {
          Set<String> names = new HashSet<>();
          for (Method m : methods) {
               checkArgs(m);
               if (names.contains(m.getName())) {
                    throw new UnsupportedOperationException(
                              "methods with same name in service, aka method overloading is not supported");
               }
               names.add(m.getName());
          }
     }

     private static void checkArgs(Method method) {
          // 检查每个方法，远程调用方法需要符合以下条件：
          // 1.参数中如果出现CommStreamPipe则只能有这一个参数
          // 2.不能出现可变参数

     }
}
