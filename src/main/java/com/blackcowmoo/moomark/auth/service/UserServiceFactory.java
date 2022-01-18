package com.blackcowmoo.moomark.auth.service;

import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Component;


@Component
public class UserServiceFactory {
  private static Map<UserServiceType, UserService> serviceEnumMap =
      new EnumMap<>(UserServiceType.class);

  UserServiceFactory() {
    serviceEnumMap.put(UserServiceType.NORMAL, new NormalUserServiceImpl());
    serviceEnumMap.put(UserServiceType.OAUTH, new OAuhUserServiceImpl());
  }

  public UserService getUserService(UserServiceType type) {
    for (UserServiceType serviceType : serviceEnumMap.keySet()) {

      if (serviceType.equals(type)) {
        return serviceEnumMap.get(type);
      }
    }
    throw new IllegalStateException("Service type not found. ");
  }
}
