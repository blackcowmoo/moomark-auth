package com.blackcowmoo.moomark.auth.util;

import org.modelmapper.ModelMapper;

public class ModelMapperUtils {
  
  private ModelMapperUtils() {
    throw new IllegalStateException("ModelMapperUtils is utility class");
  }
  
  private static ModelMapper modelMapper = new ModelMapper();

  public static ModelMapper getModelMapper() {
    return modelMapper;
  }
}
