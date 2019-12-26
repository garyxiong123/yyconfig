package com.yofish.gary.biz.service;

import com.yofish.gary.biz.domain.Department;
import com.yofish.gary.biz.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * created by zhangyingbin on 2019/12/19 0019 下午 2:41
 * description:
 */
@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Department findOneById(Long id){
        return departmentRepository.findById(id).get();
    }


}
