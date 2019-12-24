package com.yofish.apollo.controller;

import com.yofish.apollo.domain.OpenNamespaceType;
import com.yofish.apollo.model.model.OpenNamespaceTypeModel;
import com.yofish.apollo.repository.OpenNamespaceTypeRepository;
import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author WangSongJun
 * @date 2019-12-24
 */
@Slf4j
@RestController
@RequestMapping("openNamespaceType")
@Api(description = "开放命名空间的类型")
public class OpenNamespaceTypeController {
    @Autowired
    private OpenNamespaceTypeRepository openNamespaceTypeRepository;

    @GetMapping
    public Result<List<OpenNamespaceType>> getAll() {
        List<OpenNamespaceType> all = openNamespaceTypeRepository.findAll();
        return Result.ok(all);
    }

    @PostMapping
    public Result<OpenNamespaceType> create(@Valid @RequestBody OpenNamespaceTypeModel model) {
        OpenNamespaceType namespaceType = OpenNamespaceType.builder()
                .name(model.getName())
                .comment(model.getComment())
                .build();
        this.openNamespaceTypeRepository.save(namespaceType);
        return Result.ok(namespaceType);
    }

    @PutMapping("{id:\\d+}")
    public Result<OpenNamespaceType> update(@PathVariable long id, @Valid @RequestBody OpenNamespaceTypeModel model) {
        OpenNamespaceType namespaceType = OpenNamespaceType.builder()
                .id(id)
                .name(model.getName())
                .comment(model.getComment())
                .build();
        this.openNamespaceTypeRepository.save(namespaceType);
        return Result.ok(namespaceType);
    }

    @DeleteMapping("{id:\\d+}")
    public Result update(@PathVariable long id) {
        this.openNamespaceTypeRepository.deleteById(id);
        return Result.ok();
    }


}
