package com.yofish.apollo.service;

import com.yofish.apollo.domain.Namespace;
import com.yofish.apollo.repository.NamespaceRepository;
import common.dto.NamespaceDTO;
import common.exception.BadRequestException;
import common.exception.ServiceException;
import common.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author WangSongJun
 * @date 2019-12-11
 */
@Service
public class NamespaceService {
    @Autowired
    private NamespaceRepository namespaceRepository;


    public NamespaceDTO createNamespace(String env, NamespaceDTO dto) {
        Namespace entity = BeanUtils.transform(Namespace.class, dto);
        Namespace managedEntity = this.namespaceRepository.findOne(Example.of(new Namespace(dto.getAppId(), env, dto.getClusterName(), dto.getNamespaceName()))).orElse(null);
        if (managedEntity != null) {
            throw new BadRequestException("namespace already exist.");
        }

        entity = this.namespaceRepository.save(entity);

        return BeanUtils.transform(NamespaceDTO.class, entity);
    }

    public boolean isNamespaceUnique(Long appId, String cluster, String namespace) {
        Objects.requireNonNull(appId, "AppId must not be null");
        Objects.requireNonNull(cluster, "Cluster must not be null");
        Objects.requireNonNull(namespace, "Namespace must not be null");
        return Objects.isNull(namespaceRepository.findByAppIdAndClusterNameAndNamespaceName(appId, cluster, namespace));
    }


    @Transactional
    public Namespace save(Namespace entity) {
        if (!isNamespaceUnique(entity.getAppId(), entity.getClusterName(), entity.getNamespaceName())) {
            throw new ServiceException("namespace not unique");
        }
        //protection
        entity.setId(0L);
        Namespace namespace = namespaceRepository.save(entity);

        return namespace;
    }

}
