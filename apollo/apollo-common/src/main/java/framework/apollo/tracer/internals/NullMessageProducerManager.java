package framework.apollo.tracer.internals;


import framework.apollo.tracer.spi.MessageProducer;
import framework.apollo.tracer.spi.MessageProducerManager;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class NullMessageProducerManager implements MessageProducerManager {
  private static final MessageProducer producer = new NullMessageProducer();

  @Override
  public MessageProducer getProducer() {
    return producer;
  }
}
