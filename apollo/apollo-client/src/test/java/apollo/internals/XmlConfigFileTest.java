/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package apollo.internals;

import apollo.ConfigFileChangeListener;
import apollo.enums.PropertyChangeType;
import apollo.model.ConfigFileChangeEvent;
import com.google.common.util.concurrent.SettableFuture;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.enums.ConfigFileFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class XmlConfigFileTest {
  private String someNamespace;
  @Mock
  private ConfigRepository configRepository;

  @Before
  public void setUp() throws Exception {
    someNamespace = "someName";
  }

  @Test
  public void testWhenHasContent() throws Exception {
    Properties someProperties = new Properties();
    String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
    String someValue = "someValue";
    someProperties.setProperty(key, someValue);

    when(configRepository.getConfig()).thenReturn(someProperties);

    XmlConfigFile configFile = new XmlConfigFile(someNamespace, configRepository);

    assertEquals(ConfigFileFormat.XML, configFile.getConfigFileFormat());
    assertEquals(someNamespace, configFile.getNamespace());
    assertTrue(configFile.hasContent());
    assertEquals(someValue, configFile.getContent());
  }

  @Test
  public void testWhenHasNoContent() throws Exception {
    when(configRepository.getConfig()).thenReturn(null);

    XmlConfigFile configFile = new XmlConfigFile(someNamespace, configRepository);

    assertFalse(configFile.hasContent());
    assertNull(configFile.getContent());
  }

  @Test
  public void testWhenConfigRepositoryHasError() throws Exception {
    when(configRepository.getConfig()).thenThrow(new RuntimeException("someError"));

    XmlConfigFile configFile = new XmlConfigFile(someNamespace, configRepository);

    assertFalse(configFile.hasContent());
    assertNull(configFile.getContent());
  }

  @Test
  public void testOnRepositoryChange() throws Exception {
    Properties someProperties = new Properties();
    String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
    String someValue = "someValue";
    String anotherValue = "anotherValue";
    someProperties.setProperty(key, someValue);

    when(configRepository.getConfig()).thenReturn(someProperties);

    XmlConfigFile configFile = new XmlConfigFile(someNamespace, configRepository);

    assertEquals(someValue, configFile.getContent());

    Properties anotherProperties = new Properties();
    anotherProperties.setProperty(key, anotherValue);

    final SettableFuture<ConfigFileChangeEvent> configFileChangeFuture = SettableFuture.create();
    ConfigFileChangeListener someListener = new ConfigFileChangeListener() {
      @Override
      public void onChange(ConfigFileChangeEvent changeEvent) {
        configFileChangeFuture.set(changeEvent);
      }
    };

    configFile.addChangeListener(someListener);

    configFile.onRepositoryChange(someNamespace, anotherProperties);

    ConfigFileChangeEvent changeEvent = configFileChangeFuture.get(500, TimeUnit.MILLISECONDS);

    assertEquals(anotherValue, configFile.getContent());
    assertEquals(someNamespace, changeEvent.getNamespace());
    assertEquals(someValue, changeEvent.getOldValue());
    assertEquals(anotherValue, changeEvent.getNewValue());
    assertEquals(PropertyChangeType.MODIFIED, changeEvent.getChangeType());
  }

  @Test
  public void testOnRepositoryChangeWithContentAdded() throws Exception {
    Properties someProperties = new Properties();
    String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
    String someValue = "someValue";

    when(configRepository.getConfig()).thenReturn(someProperties);

    XmlConfigFile configFile = new XmlConfigFile(someNamespace, configRepository);

    assertEquals(null, configFile.getContent());

    Properties anotherProperties = new Properties();
    anotherProperties.setProperty(key, someValue);

    final SettableFuture<ConfigFileChangeEvent> configFileChangeFuture = SettableFuture.create();
    ConfigFileChangeListener someListener = new ConfigFileChangeListener() {
      @Override
      public void onChange(ConfigFileChangeEvent changeEvent) {
        configFileChangeFuture.set(changeEvent);
      }
    };

    configFile.addChangeListener(someListener);

    configFile.onRepositoryChange(someNamespace, anotherProperties);

    ConfigFileChangeEvent changeEvent = configFileChangeFuture.get(500, TimeUnit.MILLISECONDS);

    assertEquals(someValue, configFile.getContent());
    assertEquals(someNamespace, changeEvent.getNamespace());
    assertEquals(null, changeEvent.getOldValue());
    assertEquals(someValue, changeEvent.getNewValue());
    assertEquals(PropertyChangeType.ADDED, changeEvent.getChangeType());
  }

  @Test
  public void testOnRepositoryChangeWithContentDeleted() throws Exception {
    Properties someProperties = new Properties();
    String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
    String someValue = "someValue";
    someProperties.setProperty(key, someValue);

    when(configRepository.getConfig()).thenReturn(someProperties);

    XmlConfigFile configFile = new XmlConfigFile(someNamespace, configRepository);

    assertEquals(someValue, configFile.getContent());

    Properties anotherProperties = new Properties();

    final SettableFuture<ConfigFileChangeEvent> configFileChangeFuture = SettableFuture.create();
    ConfigFileChangeListener someListener = new ConfigFileChangeListener() {
      @Override
      public void onChange(ConfigFileChangeEvent changeEvent) {
        configFileChangeFuture.set(changeEvent);
      }
    };

    configFile.addChangeListener(someListener);

    configFile.onRepositoryChange(someNamespace, anotherProperties);

    ConfigFileChangeEvent changeEvent = configFileChangeFuture.get(500, TimeUnit.MILLISECONDS);

    assertEquals(null, configFile.getContent());
    assertEquals(someNamespace, changeEvent.getNamespace());
    assertEquals(someValue, changeEvent.getOldValue());
    assertEquals(null, changeEvent.getNewValue());
    assertEquals(PropertyChangeType.DELETED, changeEvent.getChangeType());
  }

  @Test
  public void testWhenConfigRepositoryHasErrorAndThenRecovered() throws Exception {
    Properties someProperties = new Properties();
    String key = ConfigConsts.CONFIG_FILE_CONTENT_KEY;
    String someValue = "someValue";
    someProperties.setProperty(key, someValue);

    when(configRepository.getConfig()).thenThrow(new RuntimeException("someError"));

    XmlConfigFile configFile = new XmlConfigFile(someNamespace, configRepository);

    assertFalse(configFile.hasContent());
    assertNull(configFile.getContent());

    configFile.onRepositoryChange(someNamespace, someProperties);

    assertTrue(configFile.hasContent());
    assertEquals(someValue, configFile.getContent());
  }
}
