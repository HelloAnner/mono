package com.anner.comm.grpc;

import static com.anner.comm.info.CommProperties.HOST;
import static com.anner.comm.info.CommProperties.PORT;
import static com.anner.comm.info.CommProperties.TYPE;
import static com.anner.comm.info.CommProperties.WEIGHT;

import com.anner.comm.common.BaseCommClient;
import com.anner.comm.common.CommClientFactory;
import com.anner.comm.grpc.utils.GRPCAdapterRegistry;
import com.anner.comm.info.CommProperties;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GRPCCommClient extends BaseCommClient {

     private static final String SELF_TYPE = "grpc";
     private static final String SECURE = "secure";

     static {
          // 类加载时将自身注册到工厂中
          CommClientFactory.registerClient(SELF_TYPE, GRPCCommClient::new);
     }

     private final ManagedChannel channel;

     public GRPCCommClient(CommProperties properties) {
          super(properties);
          String addr = properties.getAsString(HOST, "");
          int port = properties.getAsInt(PORT, 0);
          boolean secure = properties.getAsBool(SECURE);

          ManagedChannelBuilder<?> build = ManagedChannelBuilder.forAddress(addr, port);
          if (!secure) {
               build.usePlaintext();
          }
          channel = build.build();
     }

     public GRPCCommClient(String addr, int port) {
          this(parseProperties(addr, port));
     }

     @Override
     public <T> T createCaller(Class<T> callerInterface) {
          return GRPCAdapterRegistry.createStub(callerInterface, channel);
     }

     @Override
     protected void finalize() throws Throwable {
          shutdown();
     }

     public void shutdown() {
          if (channel != null) {
               channel.shutdown();
          }
     }

     private static CommProperties parseProperties(String addr, int port) {
          CommProperties p = new CommProperties();
          p.put(TYPE, SELF_TYPE);
          p.put(HOST, addr);
          p.put(PORT, port);
          p.put(WEIGHT, 1);
          p.put(SECURE, false);
          return p;
     }

}
