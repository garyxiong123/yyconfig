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
package com.yofish.apollo.repository;

import com.yofish.apollo.domain.ReleaseMessage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ReleaseMessageRepository extends PagingAndSortingRepository<ReleaseMessage, Long> {
  List<ReleaseMessage> findFirst500ByIdGreaterThanOrderByIdAsc(Long id);

  ReleaseMessage findTopByOrderByIdDesc();

  ReleaseMessage findTopByNamespaceKeyInOrderByIdDesc(Collection<String> namespaceKeys);

  List<ReleaseMessage> findFirst100ByNamespaceKeyAndIdLessThanOrderByIdAsc(String namespaceKey, Long id);

  @Query("select namespaceKey, max(id) as id from ReleaseMessage where namespaceKey in :messages group by namespaceKey")
  List<Object[]> findLatestReleaseMessagesGroupByNamespaceKeys(@Param("messages") Collection<String> messages);
}
