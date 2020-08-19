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
package apollo.component.util.http;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class HttpResponse<T> {
  private final int m_statusCode;
  private final T m_body;

  public HttpResponse(int statusCode, T body) {
    this.m_statusCode = statusCode;
    this.m_body = body;
  }

  public int getStatusCode() {
    return m_statusCode;
  }

  public T getBody() {
    return m_body;
  }
}