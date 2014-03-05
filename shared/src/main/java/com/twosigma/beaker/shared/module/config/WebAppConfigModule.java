/*
 *  Copyright 2014 TWO SIGMA INVESTMENTS, LLC
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

package com.twosigma.beaker.shared.module.config;

import com.google.inject.AbstractModule;

/**
 * WebAppConfigModule
 *
 */
public class WebAppConfigModule extends AbstractModule {
  private final WebAppConfigPref pref;

  public WebAppConfigModule(WebAppConfigPref pref) {
    this.pref = pref;
  }

  @Override
  protected void configure() {
    bind(WebAppConfigPref.class).toInstance(pref);
    bind(WebAppConfig.class).to(WebAppConfigImpl.class);
  }

}
