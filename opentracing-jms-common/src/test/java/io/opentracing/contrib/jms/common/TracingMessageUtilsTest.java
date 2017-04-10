package io.opentracing.contrib.jms.common;

import io.opentracing.SpanContext;
import io.opentracing.contrib.spanmanager.DefaultSpanManager;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.Destination;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class TracingMessageUtilsTest {
    private static final MockTracer mockTracer = new MockTracer(MockTracer.Propagator.TEXT_MAP);

    @BeforeClass
    public static void init() {
        GlobalTracer.register(mockTracer);
    }

    @Before
    public void before() {
        mockTracer.reset();
        DefaultSpanManager.getInstance().clear();
    }

    @Test
    public void noSpanToExtract() {
        SpanContext context = TracingMessageUtils.extract(new ActiveMQTextMessage());
        assertNull(context);
    }

    @Test
    public void extractContextFromManager() {
        MockSpan span = mockTracer.buildSpan("test").start();
        DefaultSpanManager.getInstance().activate(span);
        MockSpan.MockContext context = (MockSpan.MockContext) TracingMessageUtils.extract(new ActiveMQTextMessage());
        assertNotNull(context);
        assertEquals(span.context().spanId(), context.spanId());
    }

    @Test
    public void extractContextFromProperties() {
        MockSpan span = mockTracer.buildSpan("test").start();
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        TracingMessageUtils.inject(span, message);
        MockSpan.MockContext context = (MockSpan.MockContext) TracingMessageUtils.extract(message);
        assertNotNull(context);
        assertEquals(span.context().spanId(), context.spanId());
    }

    @Test
    public void buildAndFinishChildSpan() {
        MockSpan span = mockTracer.buildSpan("test").start();
        DefaultSpanManager.getInstance().activate(span);
        MockSpan span2 = (MockSpan) TracingMessageUtils.buildAndFinishChildSpan(new ActiveMQTextMessage());

        MockSpan managedSpan = (MockSpan) DefaultSpanManager.getInstance().current().getSpan();
        assertEquals("test", managedSpan.operationName());

        assertEquals(1, mockTracer.finishedSpans().size());
        assertEquals(TracingMessageUtils.OPERATION_NAME_RECEIVE, span2.operationName());
        assertEquals(Tags.SPAN_KIND_CLIENT, span2.tags().get(Tags.SPAN_KIND.getKey()));
        assertEquals(TracingMessageUtils.COMPONENT_NAME, span2.tags().get(Tags.COMPONENT.getKey()));
        assertEquals(span.context().spanId(), span2.parentId());
        assertEquals(span.context().traceId(), span2.context().traceId());
    }

    @Test
    public void inject() throws IOException {
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        assertTrue(message.getProperties().isEmpty());

        MockSpan span = mockTracer.buildSpan("test").start();
        TracingMessageUtils.inject(span, message);
        assertFalse(message.getProperties().isEmpty());
    }

    @Test
    public void buildAndInjectSpan() throws Exception {
        Destination destination = new ActiveMQQueue("queue");

        ActiveMQTextMessage message = new ActiveMQTextMessage();
        MockSpan span = mockTracer.buildSpan("test").start();
        DefaultSpanManager.getInstance().activate(span);

        MockSpan injected = (MockSpan) TracingMessageUtils.buildAndInjectSpan(destination, message);

        assertFalse(message.getProperties().isEmpty());
        assertEquals(span.context().spanId(), injected.parentId());
    }

}