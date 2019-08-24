package com.jvmrally.lambda.command.entites;

import java.util.concurrent.TimeUnit;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;

/**
 * TimedReasonRequest
 */
@ParsedEntity
public class TimedReasonRequest extends ReasonRequest {

    @Flag(shortName = 'd', longName = "days", description = "Number of days.")
    private Integer days = 0;

    @Flag(shortName = 'H', longName = "hours", description = "Number of hours.")
    private Integer hours = 1;

    /**
     * @return the days
     */
    public Integer getDays() {
        return days;
    }

    /**
     * @return the hours
     */
    public Integer getHours() {
        return hours;
    }

    public long getExpiry() {
        return System.currentTimeMillis() + TimeUnit.HOURS.toMillis(getHours())
                + TimeUnit.DAYS.toMillis(getHours());
    }
}
