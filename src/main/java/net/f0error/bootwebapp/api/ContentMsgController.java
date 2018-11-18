package net.f0error.bootwebapp.api;

import lombok.extern.slf4j.Slf4j;
import net.f0error.bootwebapp.model.ContentMsg;
import net.f0error.bootwebapp.service.ContentMsgService;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;
import java.util.List;

@Slf4j
@RestController("/api/v1/contentMsg")
public class ContentMsgController {
    private final ContentMsgService contentMsgService;

    public ContentMsgController(ContentMsgService contentMsgService) {
        this.contentMsgService = contentMsgService;
    }

    @PostMapping("/add")
    public void createContentMsgs(@RequestBody  List<ContentMsg> contentMsgs) {
        contentMsgService.sendContentMsgs(contentMsgs);
    }

    @GetMapping("/browse/{firstNmsgs}")
    public List<ContentMsg> browseContentMsgs( @PathVariable("firstNmsgs") Integer firstNmsgs) {
        return contentMsgService.browseContentMsgs(firstNmsgs);
    }

    @GetMapping("/processFirstMsg")
    public ContentMsg processContentMsg() throws JMSException {
        return contentMsgService.processContentMsg();
    }
}
