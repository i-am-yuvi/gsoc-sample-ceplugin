package org.jenkinsci.plugins.cloudeventsSample.Sinks;

import dev.cdevents.CDEvents;
import dev.cdevents.constants.CDEventConstants;
import dev.cdevents.events.PipelinerunFinishedCDEvent;
import dev.cdevents.events.TaskrunFinishedCDEvent;
import dev.cdevents.events.TaskrunStartedCDEvent;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.MessageWriter;
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
import java.util.UUID;
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



        PipelinerunFinishedCDEvent pipelinerunFinishedCDEvent =  new PipelinerunFinishedCDEvent();

        /* set the required context fields to the pipelineRunFinishedCDEvent */
        pipelinerunFinishedCDEvent.setSource(URI.create(source));

        /* set the required subject fields to the pipelineRunFinishedCDEvent */
        pipelinerunFinishedCDEvent.setSubjectId(uuid.toString());
        pipelinerunFinishedCDEvent.setSubjectSource(URI.create(source);
        pipelinerunFinishedCDEvent.setSubjectUrl(source);
        pipelinerunFinishedCDEvent.setSubjectOutcome(CDEventConstants.Outcome.SUCCESS.getOutcome());
        pipelinerunFinishedCDEvent.setSubjectPipelineName(type);
        pipelinerunFinishedCDEvent.setSubjectErrors("pipelineErrors");

        /* Create a CloudEvent from a pipelineRunFinishedCDEvent */
        CloudEvent ceEvent = CDEvents.cdEventAsCloudEvent(pipelinerunFinishedCDEvent);

        return ceEvent;


    }

    @Override
    public void sendCloudEvent(String sinkURL, Object data) throws IOException, NullPointerException {

        CloudEvent cloudEventToPost = null;

        try{
            cloudEventToPost = buildCloudEvent(data);
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        }

        URL url = new URL(sinkURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        try{
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("post");


            MessageWriter messageWriter = createMessageWriter(httpURLConnection);
            if(cloudEventToPost != null){
                messageWriter.writeBinary(cloudEventToPost);
            }
        }catch (ProtocolException p){
            p.printStackTrace();
        }

    }



}
