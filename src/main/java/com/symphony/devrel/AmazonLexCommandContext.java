package com.symphony.devrel;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmazonLexCommandContext extends CommandContext {

    /**
     * Gif category to look for.
     */
    private String message;

    /** Default required constructor */
    public AmazonLexCommandContext(V4Initiator initiator, V4MessageSent eventSource) {
        super(initiator, eventSource);
    }
}