package org.jenkinsci.plugins.cloudeventsSample.Sinks;


import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.v1.CloudEventBuilder;
import org.jenkinsci.plugins.cloudeventsSample.CloudEvents;
import org.jenkinsci.plugins.cloudeventsSample.CloudEventsSink;
import org.jenkinsci.plugins.cloudeventsSample.CloudEventsUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jenkinsci.plugins.cloudeventsSample.CloudEventMessageWriter.createMessageWriter;

public class HTTPSink extends CloudEventsSink{

    private static final Logger LOGGER = Logger.getLogger(HTTPSink.class.getName());

    public CloudEvent buildCloudEvent(Object data) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        UUID uuid = UUID.randomUUID();

        Method getType = data.getClass().getMethod("getType");
        String type = (String) getType.invoke(data);

        Method getSource = data.getClass().getMethod("getSource");
        String source = (String) getSource.invoke(data);

        String cloudEventPayLoad = CloudEventsUtil.convertToJson(data);


//        /* set the required subject fields to the pipelineRunFinishedCDEvent */
        String typeDef = "pipelineRun";
        // Constructs the CloudEvent in Binary Format
        CloudEvent cloudEvent = new CloudEventBuilder()
                .withType("org.jenkinsci.cdevents.pipelinerun.started.0.1.1")
                .withDataContentType("application/json")
                .withSubject(typeDef)
                .withSubject(typeDef).withId(uuid.toString())
                .withSubject(typeDef).withSource(URI.create(source))
                .withSubject(typeDef).withType(typeDef)
                .withSubject(typeDef).withData(cloudEventPayLoad.getBytes(StandardCharsets.UTF_8))
                .build();

        return cloudEvent;

    }

    @Override
    public void sendCloudEvent(String sinkURL, Object data) throws IOException, NullPointerException {

        CloudEvent cloudEventToPost = null;

        try{
            cloudEventToPost = buildCloudEvent(data);
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        URL url = new URL(sinkURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        try{
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");


            MessageWriter messageWriter = createMessageWriter(httpURLConnection);
            if(cloudEventToPost != null){
                messageWriter.writeBinary(cloudEventToPost);
            }
        }catch (ProtocolException p){
            p.printStackTrace();
        }

        int responseCode = httpURLConnection.getResponseCode();
        LOGGER.log(Level.INFO, String.format("Received response: %s", responseCode));

    }

}
