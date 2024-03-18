package org.jenkinsci.plugins.cloudeventsSample.Sinks;

import dev.cdevents.CDEvents;
import dev.cdevents.constants.CDEventConstants;
import dev.cdevents.events.PipelinerunFinishedCDEvent;
import dev.cdevents.events.PipelinerunStartedCDEvent;
import dev.cdevents.events.TaskrunFinishedCDEvent;
import dev.cdevents.events.TaskrunStartedCDEvent;
import dev.cdevents.models.pipelinerun.started.Subject;
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



//        PipelinerunFinishedCDEvent pipelinerunFinishedCDEvent =  new PipelinerunFinishedCDEvent();
//
//        /* set the required context fields to the pipelineRunFinishedCDEvent */
//        pipelinerunFinishedCDEvent.setSource(URI.create(source));
//
//        /* set the required subject fields to the pipelineRunFinishedCDEvent */
//        pipelinerunFinishedCDEvent.setSubjectId(uuid.toString());
//        pipelinerunFinishedCDEvent.setSubjectSource(URI.create(source));
//        pipelinerunFinishedCDEvent.setSubjectUrl(source);
//        pipelinerunFinishedCDEvent.setSubjectOutcome(CDEventConstants.Outcome.SUCCESS.getOutcome());
//        pipelinerunFinishedCDEvent.setSubjectPipelineName(type);
//        pipelinerunFinishedCDEvent.setSubjectErrors("pipelineErrors");
//        pipelinerunFinishedCDEvent.setCustomDataContentType("application/json");

//        PipelinerunStartedCDEvent pipelinerunStartedCDEvent = new PipelinerunStartedCDEvent();
//
//        pipelinerunStartedCDEvent.setSubjectId(uuid.toString());
//        pipelinerunStartedCDEvent.setSource(URI.create(source));
//        pipelinerunStartedCDEvent.setSubjectPipelineName(type);
//        pipelinerunStartedCDEvent.setSubjectSource(URI.create(source));


        /* Create a CloudEvent from a pipelineRunFinishedCDEvent */
//        CloudEvent ceEvent = CDEvents.cdEventAsCloudEvent(pipelinerunStartedCDEvent);

        // Constructs the CloudEvent in Binary Format
        CloudEvent cloudEvent = new CloudEventBuilder()
                .withId(uuid.toString())
                .withSource(URI.create(source))
                .withType(type)
                .withDataContentType("application/json")
                .withData(cloudEventPayLoad.getBytes(StandardCharsets.UTF_8))
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
