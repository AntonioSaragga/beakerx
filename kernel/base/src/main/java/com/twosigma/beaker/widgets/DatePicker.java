/*
 *  Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
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
package com.twosigma.beaker.widgets;

import com.twosigma.beaker.widgets.internal.InternalWidgetUtils;

import java.io.Serializable;
import java.util.HashMap;

public class DatePicker extends ValueWidget<String> {
  public static final String VIEW_NAME_VALUE = "DatePickerView";
  public static final String MODEL_NAME_VALUE = "DatePickerModel";
  public static final String SHOW_TIME = "showTime";

  private Boolean showTime;

  public DatePicker() {
    super();
    this.value = "";
    openComm();
  }

  @Override
  public void updateValue(Object value) {
    this.value = (String) value;
  }

  @Override
  public String getValueFromObject(Object input) {
    return (String) input;
  }

  @Override
  protected HashMap<String, Serializable> content(HashMap<String, Serializable> content) {
    super.content(content);
    content.put(MODEL_MODULE, InternalWidgetUtils.MODEL_MODULE_VALUE);
    content.put(VIEW_MODULE, InternalWidgetUtils.VIEW_MODULE_VALUE);
    content.put(VALUE, this.value);
    return content;
  }

  @Override
  public String getModelNameValue() {
    return MODEL_NAME_VALUE;
  }

  @Override
  public String getViewNameValue() {
    return VIEW_NAME_VALUE;
  }

  public void setShowTime(final Boolean showTime) {
    this.showTime = showTime;
    sendUpdate(SHOW_TIME, showTime);
  }

  public Boolean getShowTime() {
    return showTime;
  }

}