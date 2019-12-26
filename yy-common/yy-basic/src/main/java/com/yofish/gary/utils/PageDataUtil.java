///*
// *    Copyright 2018-2019 the original author or authors.
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//package jpa.utils;
//
//import com.github.pagehelper.PageInfo;
//import com.youyu.common.api.PageData;
//
//import java.util.List;
//
//import static java.util.Objects.isNull;
//import static net.atayun.bazooka.combase.utils.PageDataUtil.Builder.newBuilder;
//
///**
// * @author pqq
// * @version v1.0
// * @date 2019年6月28日 10:00:00
// * @work 分页工具类
// */
//public class PageDataUtil {
//
//    /**
//     * PageHelper的PageInfo转换为PageData
//     *
//     * @param pageInfo
//     * @param tList
//     * @param <S>
//     * @param <T>
//     * @return
//     */
//    public static <S, T> PageData<T> pageInfo2PageData(PageInfo<S> pageInfo, List<T> tList) {
//        if (isNull(pageInfo)) {
//            return null;
//        }
//
//        return newBuilder()
//                .pageNum(pageInfo.getPageNum())
//                .pageSize(pageInfo.getPageSize())
//                .totalCount(pageInfo.getTotal())
//                .totalPage(pageInfo.getPages())
//                .rows(tList)
//                .build();
//    }
//
//    /**
//     * PageData建造器
//     */
//    public final static class Builder {
//
//        /**
//         * pageData对象
//         */
//        private PageData pageData;
//
//        private Builder() {
//            this.pageData = new PageData();
//        }
//
//        /**
//         * 实例化构造器
//         *
//         * @return
//         */
//        public static Builder newBuilder() {
//            return new Builder();
//        }
//
//        /**
//         * 当前页码
//         *
//         * @param pageNum
//         * @return
//         */
//        public Builder pageNum(Integer pageNum) {
//            pageData.setPageNum(pageNum);
//            return this;
//        }
//
//        /**
//         * 每页大小
//         *
//         * @param pageSize
//         * @return
//         */
//        public Builder pageSize(Integer pageSize) {
//            pageData.setPageSize(pageSize);
//            return this;
//        }
//
//        /**
//         * 总条数
//         *
//         * @param totalCount
//         * @return
//         */
//        public Builder totalCount(Long totalCount) {
//            pageData.setTotalCount(totalCount);
//            return this;
//        }
//
//        /**
//         * 总页数
//         *
//         * @param totalPage
//         * @return
//         */
//        public Builder totalPage(Integer totalPage) {
//            pageData.setTotalPage(totalPage);
//            return this;
//        }
//
//        /**
//         * 数据
//         *
//         * @param rows
//         * @param <T>
//         * @return
//         */
//        public <T> Builder rows(List<T> rows) {
//            pageData.setRows(rows);
//            return this;
//        }
//
//        /**
//         * 建造PageData对象
//         *
//         * @param <T>
//         * @return
//         */
//        public <T> PageData<T> build() {
//            return pageData;
//        }
//    }
//}
