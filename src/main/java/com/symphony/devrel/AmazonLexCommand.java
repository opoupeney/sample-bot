package com.symphony.devrely;

import com.amazonaws.services.lexruntime.AmazonLexRuntime;
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder;
import com.amazonaws.services.lexruntime.model.PostTextRequest;
import com.amazonaws.services.lexruntime.model.PostTextResult;

import com.symphony.bdk.core.activity.command.PatternCommandActivity;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.MessageService;

import com.symphony.bdk.template.api.Template;
import com.symphony.bdk.template.api.TemplateEngine;
import com.symphony.bdk.template.api.TemplateException;
import com.symphony.devrel.AmazonLexCommandContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class AmazonLexCommand extends PatternCommandActivity<AmazonLexCommandContext> {

    private final MessageService messageService;

    final String BOT_NAME = "SampleBot";
    final String REGION = "us-east-1";
    final AmazonLexRuntime lexClient = AmazonLexRuntimeClientBuilder.standard().withRegion(REGION).build();

    public AmazonLexCommand(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Pattern pattern() {
        return Pattern.compile("^@" + this.getBotDisplayName() + " (.+)");
    }

    @Override
    protected void prepareContext(AmazonLexCommandContext context, Matcher matcher) {
        context.setMessage(matcher.group(1));
    }

    @Override
    public void onActivity(final AmazonLexCommandContext context) {
        log.info("Lex message is \"{}\"", context.getMessage());
        Map<String,String> sessionAttributes = new HashMap<String, String>();
        sessionAttributes.put("firstname", context.getInitiator().getUser().getFirstName());
        sessionAttributes.put("lastname", context.getInitiator().getUser().getLastName());
        sessionAttributes.put("email", context.getInitiator().getUser().getEmail());
        sessionAttributes.put("stream", context.getStreamId());

        // create the post text request
        PostTextRequest textRequest = new PostTextRequest();
        textRequest.setBotName(BOT_NAME);
        textRequest.setBotAlias(BOT_NAME);
        textRequest.setUserId(context.getInitiator().getUser().getUserId().toString());
        textRequest.setSessionAttributes(sessionAttributes);
        textRequest.setInputText(context.getMessage());

        // execute the post request and get the text result
        PostTextResult textResult = lexClient.postText(textRequest);

        // prepare the message
        try {
            Template template = TemplateEngine.getDefaultImplementation().newBuiltInTemplate("simpleMML");
            final String message = template.process(new HashMap<String, String>() {{
                put("message", textResult.getMessage());
            }});
            // reply to the user
            messageService.send(context.getStreamId(), message);
        } catch (TemplateException te) {
            log.error(te.getMessage());
        }
    }

    @Override
    protected ActivityInfo info() {
        final ActivityInfo info = ActivityInfo.of(ActivityType.COMMAND);
        info.setName("Amazon Lex Message Command");
        info.setDescription("Usage: @BotName {message}");
        return info;
    }
}
