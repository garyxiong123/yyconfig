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
package jpa.domain;

/**
 * 发布状态
 *
 * @author Ping
 * @date 2019-05-20
 */
public enum BasicStatusEnum {

    /**
     * 准备中
     */
    STAND_BY("未开始"),

    /**
     * 进行中
     */
    PROCESS("进行中"),

    /**
     * 完成
     */
    SUCCESS("成功"),

    /**
     * 失败
     */
    FAILURE("失败");

    private String description;

    BasicStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
