package repository;

import com.yofish.gary.api.dto.req.UserAddReqDTO;
import com.yofish.gary.biz.controller.UserController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.Valid;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/11 下午5:34
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.yofish.gary.JpaApplication.class})
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void login() {

    }

    @Test
    public void add() {

        UserAddReqDTO userAddReqDTO = createUserAddReqDTO();
        userController.add(userAddReqDTO);
    }

    private UserAddReqDTO createUserAddReqDTO() {


        UserAddReqDTO userAddReqDTO = UserAddReqDTO.builder().username("gary").password("123456").build();
        return userAddReqDTO;
    }
}