package controller;

import com.yofish.apollo.controller.AppNamespaceController;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.dto.NamespaceListReq;
import com.yofish.apollo.dto.NamespaceListResp;
import com.yofish.apollo.model.bo.NamespaceVO;
import com.youyu.common.api.Result;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author rache
 * @date 2019-12-25
 */

public class AppEnvClusterNamespaceTest extends AbstractControllerTest{
    @Autowired
    private AppNamespaceController appNamespaceController;

    @Test
    public void list(){
        NamespaceListReq req=new NamespaceListReq();
        req.setAppCode("payment");
        req.setNamespace("application");
        Result<List<NamespaceListResp>> listResult= appNamespaceController.namespaceList(req);
        System.out.println(1);
    }

    @Test
    public void findNamespaces(){
        Result<List<NamespaceVO>> nameSpace= appNamespaceController.findNamespaces("middleground1","test","default");
        System.out.println(1);
    }
}
