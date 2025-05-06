package com.yenzaga.msuser.config;

public class ApplicationConstants {
  // Regex for acceptable logins
  public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";

  public static final int PASSWORD_MIN_LENGTH = 6;
  public static final int PASSWORD_MAX_LENGTH = 100;

  public enum ApplicationNames { YENZALO, YENZACART }

  public static final String DEFAULT_APP = ApplicationNames.YENZALO.toString();
}
