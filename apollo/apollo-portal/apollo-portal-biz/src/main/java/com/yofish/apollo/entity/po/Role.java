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
@Table(name = "Role")
@SQLDelete(sql = "Update Role set isDeleted = 1 where id = ?")
@Where(clause = "isDeleted = 0")
public class Role extends BaseEntity {
  @Column(name = "RoleName", nullable = false)
  private String roleName;
}
