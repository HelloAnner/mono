package com.anner.comm.grpc.adapter;

import com.google.protobuf.ByteString;

/**
 * Created by anner on 2023/3/14
 */
public class ByteHolder {
    private final ByteString bs;

    public ByteHolder(ByteString bs) {
        this.bs = bs;
    }

    public ByteString getBs() {
        return bs;
    }
}
