/*
 *    Copyright 2018-2019 the original author or authors.
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
package jpa.dto;

/**
 * 应用相关操作类型
 *
 * @author WangSongJun
 * @date 2019-07-25
 */
public enum AppOperationEnum {

    /**
     *
     */
//    START("启动服务", AppOperationEventLogTypeEnum.APP_START, true),
//    RESTART("重启服务", AppOperationEventLogTypeEnum.APP_RESTART, true),
//    SCALE("扩/缩容服务", AppOperationEventLogTypeEnum.APP_SCALE, true),
    STOP("关闭服务"),

    DEPLOY("发布"),
    ROLLBACK("回滚"),

    PUSH_IMAGE("镜像推送"),
    DELETE_IMAGE("删除镜像");

    private String description;


    private Boolean appRunStatus;

    AppOperationEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}
