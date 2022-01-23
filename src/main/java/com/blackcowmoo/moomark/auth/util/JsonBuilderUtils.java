package com.blackcowmoo.moomark.auth.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackcowmoo.moomark.auth.model.dto.Response;
import com.blackcowmoo.moomark.auth.model.dto.ResponseHeader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class JsonBuilderUtils {
  @Autowired
  static ObjectMapper objectMapper;

  public static String buildJsonWithHeaderAndPayload(ResponseHeader header, ObjectNode payload) {
    return objectMapper.valueToTree(Response.builder().header(header).payload(payload).build())
        .toPrettyString();
  }

  public static ResponseHeader buildResponseHeader(String name, String message) {
    return ResponseHeader.builder().name(name).message(message).build();
  }

  public static ObjectNode buildResponsePayloadFromText(String[] fieldNames, String[] contents) {
    ObjectNode payload = objectMapper.createObjectNode();
    for (int i = 0; i < fieldNames.length; i++) {
      payload.put(fieldNames[i], String.valueOf(contents[i]));

    }
    return payload;
  }
}
