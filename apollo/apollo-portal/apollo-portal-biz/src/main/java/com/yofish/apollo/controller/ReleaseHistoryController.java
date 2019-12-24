package com.yofish.apollo.controller;


//import com.yofish.apollo.model.bo.ReleaseHistoryBO;
import com.yofish.apollo.component.PermissionValidator;
import com.yofish.apollo.dto.ReleaseHistoryDTO;
import com.yofish.apollo.service.ReleaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReleaseHistoryController {
    @Autowired
    private ReleaseHistoryService releaseHistoryService;
    @Autowired
    private PermissionValidator permissionValidator;

    @RequestMapping(value = "/namespaceId/{namespaceId}/releases/histories", method = RequestMethod.GET)
    public Page<ReleaseHistoryDTO> findReleaseHistoriesByNamespace(@PathVariable Long namespaceId,
                                                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                                                   @RequestParam(value = "size", defaultValue = "10") int size) {

//        if (permissionValidator.shouldHideConfigToCurrentUser(namespaceId)) {
//            return Collections.emptyList();
//        }

        return releaseHistoryService.findNamespaceReleaseHistory(namespaceId, page, size);
    }

}
