package com.gresk.modules.agenda.infrastructure.config;

import com.gresk.modules.agenda.infrastructure.google.GoogleCalendarProperties;
import com.gresk.modules.agenda.infrastructure.outlook.OutlookProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({GoogleCalendarProperties.class, OutlookProperties.class})
public class AgendaCalendarSyncModuleConfig {
}
