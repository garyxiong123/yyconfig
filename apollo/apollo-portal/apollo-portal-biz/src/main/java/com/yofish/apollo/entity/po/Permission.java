package com.yofish.apollo.entity.po;

import com.yofish.gary.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Data
@Entity
@Table(name = "Permission")
@SQLDelete(sql = "Update Permission set isDeleted = 1 where id = ?")
@Where(clause = "isDeleted = 0")
public class Permission extends BaseEntity {
  @Column(name = "PermissionType", nullable = false)
  private String permissionType;

  @Column(name = "TargetId", nullable = false)
  private String targetId;

}
