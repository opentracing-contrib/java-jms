/*
 * Copyright 2017-2018 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.opentracing.contrib.jms2;


import io.opentracing.Span;
import io.opentracing.contrib.jms.common.SpanJmsDecorator;
import javax.jms.CompletionListener;
import javax.jms.Message;

/**
 * Listener for sending messages.
 * <p>
 * If sending of the message is complete then method {@code onCompletion(Message)} is called.
 * <br/>
 * If sending of the message fails then method {@code onException(Exception)} is called.
 */
public class TracingCompletionListener implements CompletionListener {

  private final Span span;
  private final CompletionListener completionListener;

  public TracingCompletionListener(Span span, CompletionListener completionListener) {
    this.span = span;
    this.completionListener = completionListener;
  }


  @Override
  public void onCompletion(Message message) {
    try {
      completionListener.onCompletion(message);
    } finally {
      span.finish();
    }
  }

  @Override
  public void onException(Message message, Exception exception) {
    try {
      completionListener.onException(message, exception);
    } finally {
      SpanJmsDecorator.onError(exception, span);
      span.finish();
    }
  }
}
