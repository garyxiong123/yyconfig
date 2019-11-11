package jpa.controller;

import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jpa.domain.deploy.DeployCommand;
import jpa.dto.DeployCommandReqDto;
import jpa.repository.deploy.DeployCommandRepository;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.DiscriminatorValue;
import java.lang.reflect.Constructor;
import java.util.Set;

import static jpa.bean.StrategyNumBean.getClassyByClassAndNumber;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/16 上午11:09
 */

@Api(description = "发布相关接口")
@RestController
@RequestMapping("/deploy")
public class DeployController {
    ThreadLocal<String> threadLocalTest = new ThreadLocal<>();
    @Autowired
    private DeployCommandRepository deployCommandRepository;
    @ApiOperation("发布)")
    @GetMapping("/test1")
    public String test1(String userName) {
        System.out.println(threadLocalTest.get());
        return threadLocalTest.get() == null ? Thread.currentThread().getName() + "空" : Thread.currentThread().getName() + threadLocalTest.get();
    }

    @ApiOperation("发布)")
    @GetMapping("/test")
    public String test(String userName) {

        threadLocalTest.set("111");

        System.out.println(threadLocalTest.get());
        return Thread.currentThread().getName() + threadLocalTest.get();
    }


    @ApiOperation("发布)")
    @PostMapping("/deploy")
    public Result operate(@Validated @RequestBody DeployCommandReqDto deployCommandReqDto) throws Exception {
        DeployCommand deployCommand = createDeployCommand(deployCommandReqDto);
        deployCommand.execute();
        System.out.printf("ss");
        return Result.ok(null);
    }

    private DeployCommand createDeployCommand(DeployCommandReqDto deployCommandReqDto) throws Exception {
        String type = deployCommandReqDto.getType();
        Class childClass = getClassyByClassAndNumber(DeployCommand.class, type);
        Constructor constructor = childClass.getConstructor(deployCommandReqDto.getClass());
        DeployCommand deployCommand = (DeployCommand) constructor.newInstance(deployCommandReqDto);

        return deployCommand;
    }


}
