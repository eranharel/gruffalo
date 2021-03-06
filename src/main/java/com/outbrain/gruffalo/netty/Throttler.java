package com.outbrain.gruffalo.netty;


import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.outbrain.gruffalo.util.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * This little fella is in charge of pushing back / restoring inbound traffic when needed
 *
 * @author Eran Harel
 */
public class Throttler {

  private static final Logger log = LoggerFactory.getLogger(Throttler.class);

  private final ChannelGroup activeServerChannels;
  private Channel serverChannel;

  public Throttler(final ChannelGroup activeServerChannels, MetricRegistry metricRegistry) {
    this.activeServerChannels = Preconditions.checkNotNull(activeServerChannels, "activeServerChannels must not be null");
    Preconditions.checkNotNull(metricRegistry, "metricFactory must not be null");
    metricRegistry.register(name(getClass().getSimpleName(), "autoread"),
        (Gauge<Integer>) () -> serverChannel == null || !serverChannel.config().isAutoRead() ? 0 : 1);
  }

  void pushBackClients() {
    changeServerAutoRead(false);
  }

  void restoreClientReads() {
    changeServerAutoRead(true);
  }

  void changeServerAutoRead(final boolean autoread) {
    log.debug("Setting server autoread={}", autoread);

    serverChannel.config().setAutoRead(autoread);
    for (final Channel activeServerChannel : activeServerChannels) {
      activeServerChannel.config().setAutoRead(autoread);
    }
  }

  void setServerChannel(final Channel serverChannel) {
    this.serverChannel = serverChannel;
  }
}
