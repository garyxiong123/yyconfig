package controller;

import com.yofish.apollo.controller.NamespaceBranchController;
import com.yofish.apollo.controller.ReleaseController;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Main;
import com.yofish.apollo.model.model.NamespaceReleaseModel;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.ReleaseRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.yofish.apollo.DomainCreate.createAppEnvClusterNamespace4Main;


public class NamespaceBranchControllerTest extends AbstractControllerTest {

    @Autowired
    ReleaseRepository releaseRepository;
    @Autowired
    NamespaceBranchController namespaceBranchController;
    @Autowired
    private AppEnvClusterNamespaceRepository namespaceRepository;

    @Test
    public void testCreateBranch() {
        AppEnvClusterNamespace4Main namespace4Main = createAppEnvClusterNamespace4Main();
        namespaceRepository.save(namespace4Main);

        namespaceBranchController.createBranch(namespace4Main.getId());
    }


}
