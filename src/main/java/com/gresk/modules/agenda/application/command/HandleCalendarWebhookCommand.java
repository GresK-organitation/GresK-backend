package com.gresk.modules.agenda.application.command;

public record HandleCalendarWebhookCommand(String channelOrSubscriptionId, String provider,
                                            String clientStateToken) {
}
