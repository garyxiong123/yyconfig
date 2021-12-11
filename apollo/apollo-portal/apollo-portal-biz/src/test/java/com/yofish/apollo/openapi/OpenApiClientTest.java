package com.yofish.apollo.openapi;

import com.yofish.gary.utils.StringUtil;
import com.yofish.platform.yyconfig.openapi.client.ApolloOpenApiClient;
import com.yofish.platform.yyconfig.openapi.dto.NamespaceReleaseDTO;
import com.yofish.platform.yyconfig.openapi.dto.OpenItemDTO;
import org.junit.Test;

/**
 * @author WangSongJun
 * @date 2021-12-11
 */
public class OpenApiClientTest {

    @Test
    public void sceneTest() {
        this.getRules();
        this.increaseTheConfiguration();
        this.releaseConfiguration();
    }

    @Test
    public void getRules() {
        String appId = "payment";
        String flowDataId = "dbName";
        OpenItemDTO itemDTO = apolloOpenApiClient().getItem(appId, "DEV", "default", "application", flowDataId);
        String rules = itemDTO.getValue();

        if (StringUtil.isEmpty(rules)) {
            System.out.println("not found!");
        }
        System.out.println(rules);
    }

    @Test
    public void increaseTheConfiguration() {
        // Increase the configuration
        String appId = "payment";
        String flowDataId = "dbName";
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey(flowDataId);
        openItemDTO.setValue("payment");
        openItemDTO.setComment("Program auto-join");
        openItemDTO.setDataChangeCreatedBy("apollo");
        apolloOpenApiClient().createOrUpdateItem(appId, "DEV", "default", "application", openItemDTO);

    }

    @Test
    public void releaseConfiguration() {
        // Release configuration
        String appId = "payment";
        NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
        namespaceReleaseDTO.setEmergencyPublish(true);
        namespaceReleaseDTO.setReleaseComment("Modify or add configurations");
        namespaceReleaseDTO.setReleasedBy("apollo");
        namespaceReleaseDTO.setReleaseTitle("Modify or add configurations");
        apolloOpenApiClient().publishNamespace(appId, "DEV", "default", "application", namespaceReleaseDTO);
    }

    public ApolloOpenApiClient apolloOpenApiClient() {
        ApolloOpenApiClient client = ApolloOpenApiClient.newBuilder()
                .withPortalUrl("http://localhost:8084")
                .withToken("token")
                .build();
        return client;
    }
}
