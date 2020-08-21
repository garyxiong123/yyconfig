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
package com.yofish.apollo.api.model.bo;


import com.yofish.yyconfig.common.common.entity.EntityPair;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class ReleaseHistoryBO {

  private long id;

  private String appId;

  private String clusterName;

  private String namespaceName;

  private String branchName;

  private String operator;

  private long releaseId;

  private String releaseTitle;

  private String releaseComment;

  private Date releaseTime;

  private String releaseTimeFormatted;

  private List<EntityPair<String>> configuration;

  private long previousReleaseId;

  private int operation;

  private Map<String, Object> operationContext;

}
