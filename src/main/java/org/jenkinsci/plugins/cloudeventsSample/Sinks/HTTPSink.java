package org.jenkinsci.plugins.cloudeventsSample.Sinks;

import io.cloudevents.CloudEvent;
import org.jenkinsci.plugins.cloudeventsSample.CloudEvents;
import org.jenkinsci.plugins.cloudeventsSample.CloudEventsSink;
import org.jenkinsci.plugins.cloudeventsSample.CloudEventsUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Logger;

public class HTTPSink extends CloudEventsSink{

    private static final Logger LOGGER = Logger.getLogger(HTTPSink.class.getName());

    public CloudEvent buildCloudEvent(Object data) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        UUID uuid = UUID.randomUUID();

        Method getType = data.getClass().getMethod("getType");
        String type = (String) getType.invoke(data);

        Method getSource = data.getClass().getMethod("getSource");
        String source = (String) getSource.invoke(data);

        String cloudEventPayLoad = CloudEventsUtil.convertToJson(data);

    }

    @Override
    public void sendCloudEvent(String sinkURL, Object data) throws IOException, NullPointerException {

    }



}
