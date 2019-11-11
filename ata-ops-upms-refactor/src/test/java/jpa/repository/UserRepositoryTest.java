package jpa.repository;

import com.google.common.collect.Sets;
import jpa.domain.upms.Permission;
import jpa.domain.upms.Role;
import jpa.domain.upms.User;
import jpa.strategy.ShiroConcurrentSessionStrategy;
import jpa.strategy.ShiroConcurrentSessionStrategy4KeepOne;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jpa.bean.StrategyNumBean.getBeanByClass;
import static jpa.bean.StrategyNumBean.getBeanByClass4Context;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/8 下午1:57
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Before
    public void before() {
        System.getProperties().setProperty("spring.application.name", "ss");
    }

    @Rollback(false)
    @Test
    public void saveTest() throws Exception {
        User user = createUser();
        userRepository.save(user);
        userRepository.findAll();
        userRepository.findById(1L);

        System.out.println("11");
    }

    private User createUser() {
        User user = User.builder().phone("1222").build();
        Set<Role> roles = createRoles();
        ShiroConcurrentSessionStrategy shiroConcurrentSessionStrategy = new ShiroConcurrentSessionStrategy4KeepOne();
        user.setShiroConcurrentSessionStrategy(shiroConcurrentSessionStrategy);
        user.setRoles(roles);
        return user;
    }

    @Test
    public void saveStrategyTest() throws Exception {
        User user = createUser();
        userRepository.save(user);

        Assert.assertNotNull(userRepository.findAll().get(0).getShiroConcurrentSessionStrategy());
    }


    @Test
    public void saveBaseEntityTest() throws Exception {
        User user = createUser();
        userRepository.save(user);
        Assert.assertNotNull(user.getCreateAuthor());

    }

    @Rollback(false)
    @Test
    public void saveStatusTest() throws Exception {
    }

    @Rollback(false)
    @Test
    public void getRepositoryByFactoryTest() {
        List<User> all = getBeanByClass4Context(UserRepository.class).findAll();
        System.out.println(all);

    }

    @Rollback(false)
    @Test
    public void getPageTest() throws Exception {
    }


    @Test
    public void findAllTest() {
        List<User> users = userRepository.findAll();
        System.out.println(users);
    }


    @Test
    public void findSortAllTest() {
        Sort s = new Sort(Sort.Direction.DESC, "id");

        //分页
        /**
         * 第一个参数：当前页：  从0开始，0代表第一页；1代表第二页；2代表第三页........
         * 第二个参数：页大小
         * 第三个参数：排序对象，可以为空
         */

        Pageable p = new PageRequest(0, 2, s);

        //查询
//        Page<Customer> page = c ustomerDao.findAll(p);

        Page<User> page = userRepository.findAll(p);
        System.out.println(page);
        System.out.println("总记录数：" + page.getTotalElements());
        System.out.println("总页数：" + page.getTotalPages());

    }

    private Set<Role> createRoles() {
        Set<Permission> permissions = createPermissions();

        Role role = Role.builder().roleName("普通角色").permissions(permissions).build();
        Set<Role> roles = Sets.newHashSet(role);

        return roles;
    }

    private Set<Permission> createPermissions() {
        Set<Permission> permissions = new HashSet<>();
        Permission permission = Permission.builder().permissionName("新增").build();
        permissions.add(permission);
        return permissions;
    }

}