package com.symphony.devrel;

import com.symphony.bdk.core.SymphonyBdk;

import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.devrely.AmazonLexCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromClasspath;

/**
 * Sample Bot
 *
 */
@Slf4j
public class App {
    public static void main( String[] args ) throws Exception {
        final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml" ));

        bdk.activities().register(new AmazonLexCommand(bdk.messages()));

        // display activities documentation in the console
        reportActivities(bdk.activities());

        // finally, start the datafeed loop
        bdk.datafeed().start();
    }

    private static void reportActivities(ActivityRegistry registry) {
        final List<ActivityInfo> infos = registry.getActivityList()
                .stream()
                .map(AbstractActivity::getInfo)
                .collect(Collectors.toList());

        log.info("--");
        log.info("-- ACTIVITIES REPORT --");
        for (ActivityInfo info : infos) {
            log.info("--");
            log.info("TYPE: {}", info.getType());
            log.info("NAME: {}", info.getName());
            log.info("DESC: {}", info.getDescription());
        }
        log.info("--");
    }
}
