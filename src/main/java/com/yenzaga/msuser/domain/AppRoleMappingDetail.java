package com.yenzaga.msuser.domain;

public class AppRoleMappingDetail {
  private String id;
  private String appId;
  private String roleId;
  private String appName;
  private String appDescription;
  private String roleName;
  private String roleDescription;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getRoleId() {
    return roleId;
  }

  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getAppDescription() {
    return appDescription;
  }

  public void setAppDescription(String appDescription) {
    this.appDescription = appDescription;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public String getRoleDescription() {
    return roleDescription;
  }

  public void setRoleDescription(String roleDescription) {
    this.roleDescription = roleDescription;
  }

  public static AppRoleMappingDetail of(String appRole) {
    AppRoleMappingDetail appRoleMappingDetail = new AppRoleMappingDetail();
    appRoleMappingDetail.setId(appRole);

    String[] rolenameParts = appRole.split("_");
    if (rolenameParts.length >= 2) {
      appRoleMappingDetail.setAppId(rolenameParts[1]);
      appRoleMappingDetail.setAppName(rolenameParts[1]);

      if (rolenameParts.length == 2) {
        appRoleMappingDetail.setRoleName(rolenameParts[1] + "_USER");
        appRoleMappingDetail.setRoleName(rolenameParts[1] + " USER");
      } else {
        appRoleMappingDetail.setRoleId(rolenameParts[1] + "_" + rolenameParts[2]);
        appRoleMappingDetail.setRoleName(rolenameParts[1] + " " + rolenameParts[2]);
      }
    }
    return appRoleMappingDetail;
  }
}
