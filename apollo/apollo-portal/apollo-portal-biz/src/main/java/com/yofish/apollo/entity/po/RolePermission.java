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
@Table(name = "RolePermission")
@SQLDelete(sql = "Update RolePermission set isDeleted = 1 where id = ?")
@Where(clause = "isDeleted = 0")
public class RolePermission extends BaseEntity {
  @Column(name = "RoleId", nullable = false)
  private long roleId;

  @Column(name = "PermissionId", nullable = false)
  private long permissionId;

}
