package com.anner.comm.grpc.adapter;

import io.grpc.stub.StreamObserver;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArgument;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

public class InputStream2ObserverTransmitterTest {

    @Test
    public void transmit() throws IOException {
        StreamObserver<ByteHolder> so = mock(StreamObserver.class);
        so.onNext(anyObject(ByteHolder.class));
        expectLastCall().andAnswer(() -> {
            ByteHolder bh = getCurrentArgument(0);
            assertEquals("hello world", bh.getBs().toString(StandardCharsets.UTF_8));
            return null;
        }).once();
        so.onCompleted();
        expectLastCall().once();
        replay(so);

        ByteArrayInputStream in = new ByteArrayInputStream("hello world".getBytes(StandardCharsets.UTF_8));
        InputStream2ObserverTransmitter<ByteHolder> tx = new InputStream2ObserverTransmitter<>(ByteHolder::new, in);
        tx.transmit(so);
        verify(so);
    }
}