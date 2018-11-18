package net.f0error.bootwebapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.f0error.bootwebapp.model.ContentMsg;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@Getter
@Service
public class ContentMsgService {
    public static final String CONTENT_MSG_OUTBOUND_Q = "contentMsg-outbound-Q";
    public static final String CONTENT_MSG_INBOUND_Q = "contentMsg-inbound-Q";
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public ContentMsgService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendContentMsgs(List<ContentMsg> contentMsgs) {
        log.info("{}", contentMsgs);
        contentMsgs.stream()
                    .forEach(cm -> {
                        log.info("ID[{}] -> {}", cm.getId(), cm);
                        getJmsTemplate().convertAndSend(CONTENT_MSG_INBOUND_Q,cm);
                    });
    }

    public List<ContentMsg> browseContentMsgs(int firstNmsgs) {
        List<ContentMsg> contentMsgs = null;

        contentMsgs = getJmsTemplate().execute(session -> {
            List<ContentMsg> contMsgs = new ArrayList<>();
            Queue contMsgQ = session.createQueue(CONTENT_MSG_INBOUND_Q);
            QueueBrowser contMsgQBrowser = session.createBrowser(contMsgQ);

            Enumeration contMsgQEnumerator = contMsgQBrowser.getEnumeration();
            int remainingMsgToBeBrowsed = firstNmsgs;

            while(contMsgQEnumerator.hasMoreElements() && (remainingMsgToBeBrowsed > 0 || remainingMsgToBeBrowsed < 0)) {
                TextMessage msg = TextMessage.class.cast(contMsgQEnumerator.nextElement());
                try {
                    ContentMsg contMsg = getMapper().readValue(msg.getText(), ContentMsg.class);
                    contMsgs.add(contMsg);
                } catch (IOException e) {
                    log.warn("Unable to process msg [{}]",msg.getText(),e);
                }
                
                --remainingMsgToBeBrowsed;
            }

            return contMsgs;
        }, true);

        log.info("Browsed messages --> {}", contentMsgs);

        return contentMsgs;
    }

    public ContentMsg processContentMsg() throws JMSException {
        ContentMsg contentsMsg = null;
//        getJmsTemplate().setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);

        Message msg = getJmsTemplate().receive(CONTENT_MSG_INBOUND_Q);

        if (msg instanceof TextMessage) {
            TextMessage txtMsg = TextMessage.class.cast(msg);
            try {
                contentsMsg = getMapper().readValue(txtMsg.getText(), ContentMsg.class);
                msg.acknowledge();
            } catch (IOException e) {
                log.warn("Unable to process msg [{}]",txtMsg.getText(),e);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
//        contentsMsg = getJmsTemplate().execute(session -> {
//            ContentMsg contMsg = null;
//            MessageConsumer contentsMsgConsumer = session.createConsumer(getJmsTemplate().getDestinationResolver().resolveDestinationName(session, CONTENT_MSG_INBOUND_Q, false));
//            Message msg = contentsMsgConsumer.receive(5000);
//
//            try {
//            if (msg instanceof TextMessage) {
//                TextMessage txtMsg = TextMessage.class.cast(msg);
//
//                    contMsg = getMapper().readValue(txtMsg.getText(), ContentMsg.class);
//                    msg.acknowledge();
//
//            }
//            } catch (IOException e) {
//                log.warn("Unable to process msg [{}]",TextMessage.class.cast(msg).getText(),e);
//            } finally {
//                contentsMsgConsumer.close();
//            }
//
//            return contMsg;
//        });

        log.info("Processed --> {}", contentsMsg);
        return contentsMsg;
    }

    //@JmsListener(destination = CONTENT_MSG_INBOUND_Q)
    public void receiveContentMsgInbound(
            @Payload ContentMsg contentMsg, @Headers MessageHeaders headers,
            Message message, Session session) {

        log.info("received <{}>", contentMsg);

        log.info("- - - - - - - - - - - - - - - - - - - - - - - -");
        log.info("######          Message Details           #####");
        log.info("- - - - - - - - - - - - - - - - - - - - - - - -");
        log.info("headers: {}", headers);
        log.info("message: {}", message);
        log.info("session: {}", session);
        log.info("- - - - - - - - - - - - - - - - - - - - - - - -");

        getJmsTemplate().convertAndSend(CONTENT_MSG_OUTBOUND_Q, contentMsg);
    }
}
