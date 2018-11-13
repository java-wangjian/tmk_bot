package com.zxxkj.freeswitch.server;

import org.freeswitch.esl.client.IEslEventListener;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventEslInboundTest {
    private static final Logger log = LoggerFactory.getLogger(EventEslInboundTest.class);
    private static String host = "127.0.0.1";
    private static int port = 8021;
    private static String password = "ClueCon";

    public static Client inBand() {

        final Client client = new Client();
        try {
            client.connect(host, port, password, 20);
        } catch (InboundConnectionFailure e) {
            log.error("Connect failed", e);
            return null;
        }

        // 注册事件处理程序
        client.addEventListener(new IEslEventListener() {
            public void eventReceived(EslEvent event) {
                // System.out.println("Event received [{}]" +
                // event.getEventHeaders());
                // 记录接听次数和时间
                if (event.getEventName().equals("CHANNEL_ANSWER")) {
                    System.err.println("CHANNEL_ANSWER");
                }
                if (event.getEventName().equals("CHANNEL_BRIDGE")) {
                    System.err.println("CHANNEL_BRIDGE");
                }

                if (event.getEventName().equals("CHANNEL_DESTROY")) {
                    System.err.println("CHANNEL_DESTROY");
                }

                if (event.getEventName().equals("CHANNEL_HANGUP_COMPLETE")) {
                    System.err.println("CHANNEL_HANGUP_COMPLETE");
                }

            }

            public void backgroundJobResultReceived(EslEvent event) {
                String uuid = event.getEventHeaders().get("Job-UUID");
                log.info("Background job result received+:" + event.getEventName() + "/" + event.getEventHeaders());// +"/"+JoinString(event.getEventHeaders())+"/"+JoinString(event.getEventBodyLines()));
            }

        });
        client.setEventSubscriptions("plain", "all");
        return client;
    }

    public static void main(String[] args) {
        Client client = inBand();
        dialPhone(client, "1000");
    }

    public static void dialPhone(Client client, String mobile){
        if(client != null){
            client.sendSyncApiCommand( "bgapi originate", "sofia/external/" + mobile + "@FreeSwitch的IP &playback(ivr/8000/mydoctestv2.wav)" );
            //String response = client.sendAsyncApiCommand( "originate", "sofia/external/" + mobile + "@192.168.188.222 &playback(ivr/8000/mydoctestv2.wav)" );  //mydoctestv2
            // System.err.println("reponse--->" + response);
        }
    }
}
