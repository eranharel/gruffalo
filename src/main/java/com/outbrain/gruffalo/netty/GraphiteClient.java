package com.outbrain.gruffalo.netty;

import io.netty.buffer.ByteBuf;

/**
 * @author Eran Harel
 */
public interface GraphiteClient {

  /**
   * Connects to the graphite relay
   */
  void connect();

  boolean publishMetrics(ByteBuf metrics);

  /**
   * Notifies the client that the incoming requests are suspended due to slow writes
   */
  void onPushBack();

  void close() throws InterruptedException;
}
