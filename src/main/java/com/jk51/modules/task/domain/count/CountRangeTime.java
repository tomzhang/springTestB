package com.jk51.modules.task.domain.count;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public class CountRangeTime {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }
}
