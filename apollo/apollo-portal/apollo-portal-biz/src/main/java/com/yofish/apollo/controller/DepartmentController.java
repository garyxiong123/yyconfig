package com.yofish.apollo.controller;

import com.yofish.apollo.domain.Department;
import com.yofish.apollo.repository.DepartmentRepository;
import com.youyu.common.api.Result;
import com.youyu.common.utils.YyAssert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 部门管理
 *
 * @author WangSongJun
 * @date 2019-12-05
 */
@Slf4j
@RestController
@RequestMapping("department")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @PostMapping
    public Result<Department> create(String departmentName) {
        Department byName = this.departmentRepository.findByName(departmentName);
        YyAssert.paramCheck(!ObjectUtils.isEmpty(byName), "部门名称不能重复");
        Department department = Department.builder().name(departmentName).build();
        this.departmentRepository.save(department);
        return Result.ok(department);
    }

    @PutMapping("{departmentId:\\d+}")
    public Result<Department> update(@PathVariable Long departmentId, String departmentName) {
        YyAssert.paramCheck(StringUtils.isEmpty(departmentId), "departmentName 不能为空");

        Department department = this.departmentRepository.findById(departmentId).orElse(null);
        YyAssert.paramCheck(ObjectUtils.isEmpty(department), "部门ID不存在");


        Department byName = this.departmentRepository.findByName(departmentName);
        YyAssert.paramCheck(!ObjectUtils.isEmpty(byName) && !departmentId.equals(byName.getId()), "部门名称不能重复");

        department.setName(departmentName);
        this.departmentRepository.save(department);
        return Result.ok(department);
    }

    @GetMapping("{departmentId:\\d+}")
    public Result<Department> select(@PathVariable Long departmentId) {
        Department department = this.departmentRepository.findById(departmentId).orElse(null);
        YyAssert.paramCheck(ObjectUtils.isEmpty(department), "部门不存在或已删除");
        return Result.ok(department);
    }

    @DeleteMapping("{departmentId:\\d+}")
    public Result<Department> delete(@PathVariable Long departmentId) {
        Department department = this.departmentRepository.findById(departmentId).orElse(null);
        YyAssert.paramCheck(ObjectUtils.isEmpty(department), "部门不存在或已删除");

        this.departmentRepository.delete(department);
        return Result.ok();
    }
}
