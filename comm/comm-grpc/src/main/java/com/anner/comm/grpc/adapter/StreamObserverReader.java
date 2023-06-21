package com.anner.comm.grpc.adapter;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.grpc.stub.StreamObserver;

/**
 * 读取StreamObserver异步结果的辅助类，用来简化异步grpc的调用代码
 */
public class StreamObserverReader<R> implements StreamObserver<R> {

     private final CountDownLatch done = new CountDownLatch(1);
     private final List<R> resultList = new ArrayList<>();
     private Throwable throwable;

     @Override
     public void onNext(R value) {
          resultList.add(value);
     }

     @Override
     public void onError(Throwable t) {
          this.throwable = t;
     }

     @Override
     public void onCompleted() {
          done.countDown();
     }

     /**
      * 等待读取结束
      */
     public void waitUntilComplete() throws InterruptedException {
          done.await();
     }

     /**
      * 获取单个结果，对于明确知道只有一个值的情况比较有用
      * 
      * @return 第一个结果
      */
     public R getSingle() throws Throwable {
          checkError();
          return !resultList.isEmpty() ? resultList.get(0) : null;
     }

     /**
      * 获取单个结果，对于明确知道只有一个值的情况比较有用
      * 
      * @param expClazz 期望抛出的异常类型
      * @return 第一个结果
      */
     public <E extends Throwable> R getSingle(Class<E> expClazz) throws E {
          checkError(expClazz);
          return !resultList.isEmpty() ? resultList.get(0) : null;
     }

     /**
      * 获取所有结果
      * 
      * @return 所有结果
      */
     public List<R> getAll() throws Throwable {
          checkError();
          return resultList;
     }

     /**
      * 获取所有结果
      * 
      * @param expClazz 期望抛出的异常类型
      * @return 所有结果
      */
     public <E extends Throwable> List<R> getAll(Class<E> expClazz) throws E {
          checkError(expClazz);
          return resultList;
     }

     private void checkError() throws Throwable {
          if (throwable != null) {
               throw throwable;
          }
     }

     @SuppressWarnings("unchecked")
     private <E extends Throwable> void checkError(Class<E> clazz) throws E {
          if (throwable != null) {
               if (clazz.isInstance(throwable)) {
                    throw (E) throwable;
               }
               E exp;
               try {
                    Constructor<E> con = clazz.getConstructor(Throwable.class);
                    exp = con.newInstance(throwable);
               } catch (Exception ignore) {
                    throw new RuntimeException(throwable);
               }
               throw exp;
          }
     }

}
