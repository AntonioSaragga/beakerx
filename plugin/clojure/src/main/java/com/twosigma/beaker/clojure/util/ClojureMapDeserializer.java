/*
 *  Copyright 2014 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.twosigma.beaker.clojure.util;

import com.twosigma.beaker.jvm.serialization.BeakerObjectConverter;
import com.twosigma.beaker.jvm.serialization.ObjectDeserializer;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import clojure.lang.PersistentArrayMap;
import org.slf4j.LoggerFactory;

public class ClojureMapDeserializer implements ObjectDeserializer {
  private final BeakerObjectConverter parent;

  public ClojureMapDeserializer(BeakerObjectConverter p) {
    parent = p;
  }

  @Override
  public boolean canBeUsed(JsonNode n) {
    return n.isObject() && (!n.has("type") || !parent.isKnownBeakerType(n.get("type").asText()));
  }

  @Override
  public Object deserialize(JsonNode n, ObjectMapper mapper) {
    HashMap<String, Object> map = new HashMap<String, Object>();
    Iterator<Map.Entry<String, JsonNode>> entries = n.getFields();
    while (entries.hasNext()) {
      try {
        Map.Entry<String, JsonNode> ee = entries.next();
        map.put(ee.getKey(), parent.deserialize(ee.getValue(), mapper));
      } catch (Exception e) {
        LoggerFactory.getLogger(this.getClass().getName()).error(e.getMessage());
      }
    }
    return PersistentArrayMap.create(map);
  }
}
