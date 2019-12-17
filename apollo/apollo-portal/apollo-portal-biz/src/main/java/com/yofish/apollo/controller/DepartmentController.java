package com.yofish.apollo.controller;

import com.yofish.apollo.domain.Department;
import com.yofish.apollo.model.DepartmentModel;
import com.yofish.apollo.model.DepartmentUpdateModel;
import com.yofish.apollo.repository.DepartmentRepository;
import com.youyu.common.api.Result;
import com.youyu.common.utils.YyAssert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 部门管理
 *
 * @author WangSongJun
 * @date 2019-12-05
 */
@Slf4j
@RestController
@RequestMapping("department")
@Api(description = "部门")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @PostMapping
    @ApiOperation("创建部门")
    public Result<Department> create(@RequestBody @Valid DepartmentModel model) {
        Department byName = this.departmentRepository.findByName(model.getName());
        YyAssert.paramCheck(!ObjectUtils.isEmpty(byName), "部门名称不能重复");
        Department department = this.modelToEntity(model);
        this.departmentRepository.save(department);
        return Result.ok(department);
    }

    @PutMapping("{departmentId:\\d+}")
    @ApiOperation("修改部门名称")
    public Result<Department> update(@PathVariable Long departmentId,@RequestBody DepartmentUpdateModel model) {

        Department department = this.departmentRepository.findById(departmentId).orElse(null);
        YyAssert.paramCheck(ObjectUtils.isEmpty(department), "部门ID不存在");

        Department byName = this.departmentRepository.findByName(model.getName());
        YyAssert.paramCheck(!ObjectUtils.isEmpty(byName) && !departmentId.equals(byName.getId()), "部门名称不能重复");

        department.setName(model.getName());
        department.setComment(model.getComment());
        this.departmentRepository.save(department);
        return Result.ok(department);
    }

    @GetMapping("{departmentId:\\d+}")
    public Result<Department> select(@PathVariable Long departmentId) {
        Department department = this.departmentRepository.findById(departmentId).orElse(null);
        YyAssert.paramCheck(ObjectUtils.isEmpty(department), "部门不存在或已删除");
        return Result.ok(department);
    }


    @GetMapping
    public Result<List<Department>> selectAll() {
        List<Department> departmentList = this.departmentRepository.findAll();
        return Result.ok(departmentList);
    }

    @DeleteMapping("{departmentId:\\d+}")
    public Result<Department> delete(@PathVariable Long departmentId) {
        Department department = this.departmentRepository.findById(departmentId).orElse(null);
        YyAssert.paramCheck(ObjectUtils.isEmpty(department), "部门不存在或已删除");

        this.departmentRepository.delete(department);
        return Result.ok();
    }

    private Department modelToEntity(DepartmentModel model) {
        return Department.builder().name(model.getName()).code(model.getCode()).build();
    }
}
