package com.yofish.apollo.controller;

import com.yofish.apollo.dto.InstanceDTO;
import com.yofish.apollo.util.PageQuery;
import com.youyu.common.api.Result;
import common.dto.PageDTO;
import controller.AbstractControllerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @Author: xiongchengwei
 * @Date: 2020/1/2 下午1:40
 */
public class InstanceControllerTest extends AbstractControllerTest {

    @Autowired
    private InstanceController instanceController;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getByRelease() {

        PageQuery<Long> longPageQuery = new PageQuery();
        Result<PageDTO<InstanceDTO>> byRelease = instanceController.getByRelease(longPageQuery);

    }

    @Test
    public void getByNamespace() {
    }

    @Test
    public void getInstanceCountByNamespace() {
    }

    @Test
    public void getByReleasesNotIn() {
    }
}