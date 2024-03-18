package org.jenkinsci.plugins.cloudeventsSample;

import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.http.HttpMessageFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;

public class CloudEventMessageWriter {


    public static MessageWriter createMessageWriter(HttpURLConnection httpURLConnection){
        return HttpMessageFactory.createWriter(
              httpURLConnection::setRequestProperty,
                body -> {
                  try{
                      if(body != null) {
                          httpURLConnection.setRequestProperty("content-length", String.valueOf(body.length));
                          try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                              outputStream.write(body);
                          }
                      }else{
                          httpURLConnection.setRequestProperty("content-length", "0");
                      }
                  }catch (IOException i){
                      throw new UncheckedIOException(i);
                  }
                }
        );
    }


}
