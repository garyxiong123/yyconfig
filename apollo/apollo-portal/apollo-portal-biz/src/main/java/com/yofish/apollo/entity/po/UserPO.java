package com.yofish.apollo.entity.po;


import com.yofish.apollo.entity.bo.UserInfo;
import lombok.Data;

import javax.persistence.*;

/**
 * @author lepdou 2017-04-08
 */
@Data
@Entity
@Table(name = "Users")
public class UserPO {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "Id")
  private long id;
  @Column(name = "Username", nullable = false)
  private String username;
  @Column(name = "Password", nullable = false)
  private String password;
  @Column(name = "Email", nullable = false)
  private String email;
  @Column(name = "Enabled", nullable = false)
  private int enabled;

  public UserInfo toUserInfo() {
    UserInfo userInfo = new UserInfo();
    userInfo.setName(this.getUsername());
    userInfo.setUserId(this.getUsername());
    userInfo.setEmail(this.getEmail());
    return userInfo;
  }
}
