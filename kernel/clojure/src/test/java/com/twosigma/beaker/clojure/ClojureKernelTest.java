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
package com.twosigma.beaker.clojure;

import com.twosigma.beaker.KernelSocketsServiceTest;
import com.twosigma.beaker.jupyter.comm.Comm;
import com.twosigma.jupyter.KernelParameters;
import com.twosigma.jupyter.KernelRunner;
import com.twosigma.jupyter.message.Message;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.twosigma.beaker.MessageFactoryTest.getExecuteRequestMessage;
import static com.twosigma.beaker.evaluator.EvaluatorResultTestWatcher.waitForResult;
import static org.assertj.core.api.Assertions.assertThat;

public class ClojureKernelTest {

  private ClojureKernel kernel;

  private KernelSocketsServiceTest kernelSocketsService;

  @Before
  public void setUp() throws Exception {
    String sessionId = "sessionId1";
    ClojureEvaluator evaluator = new ClojureEvaluator(sessionId, sessionId);
    evaluator.setShellOptions(kernelParameters());
    kernelSocketsService = new KernelSocketsServiceTest();
    kernel = new ClojureKernel(sessionId, evaluator, kernelSocketsService);
    new Thread(() -> KernelRunner.run(() -> kernel)).start();
    kernelSocketsService.waitForSockets();
  }

  @After
  public void tearDown() throws Exception {
    kernelSocketsService.shutdown();
  }

  //@Test
  public void evaluate() throws Exception {
    //given
    String code = "" +
            "(def fib-seq-lazy \n" +
            "  ((fn rfib [a b] \n" +
            "     (lazy-seq (cons a (rfib b (+ a b)))))\n" +
            "   0 1))\n" +
            "(take 20 fib-seq-lazy)";
    Message message = getExecuteRequestMessage(code);
    //when
    kernelSocketsService.handleMsg(message);
    Optional<Message> result = waitForResult(kernelSocketsService.getKernelSockets());
    //then
    verifyResult(result);
  }

  private void verifyResult(Optional<Message> result) {
    assertThat(result).isPresent();
    Message message = result.get();
    Map actual = ((Map) message.getContent().get(Comm.DATA));
    String value = (String) actual.get("text/plain");
    assertThat(value).contains("[0,1,1,2,3,5");
  }

  private KernelParameters kernelParameters() {
    Map<String, Object> params = new HashMap<>();
    return new KernelParameters(params);
  }
}