package framework.apollo.tracer.internals;


import framework.apollo.core.utils.ClassLoaderUtil;
import framework.apollo.tracer.internals.cat.CatMessageProducer;
import framework.apollo.tracer.internals.cat.CatNames;
import framework.apollo.tracer.spi.MessageProducer;
import framework.apollo.tracer.spi.MessageProducerManager;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultMessageProducerManager implements MessageProducerManager {
  private static MessageProducer producer;

  public DefaultMessageProducerManager() {
    if (ClassLoaderUtil.isClassPresent(CatNames.CAT_CLASS)) {
      producer = new CatMessageProducer();
    } else {
      producer = new NullMessageProducerManager().getProducer();
    }
  }

  @Override
  public MessageProducer getProducer() {
    return producer;
  }
}
