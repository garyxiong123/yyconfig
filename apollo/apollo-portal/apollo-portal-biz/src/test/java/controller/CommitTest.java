package controller;

import com.yofish.apollo.controller.CommitController;
import com.yofish.apollo.dto.CommitDto;
import com.youyu.common.api.Result;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author rache
 * @date 2019-12-26
 */
public class CommitTest extends AbstractControllerTest {
    @Autowired
    private CommitController commitController;

    @Test
    public void find(){
        Result<List<CommitDto>> result= commitController.find(44L);
        System.out.println();
    }
}
