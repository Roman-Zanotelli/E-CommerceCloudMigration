package org.yearup.observe;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class Metrics {
    @Autowired
    private MeterRegistry meterRegistry;
    static private final ConcurrentMap<String, Counter> counters = new ConcurrentHashMap<>();
    public void increment(String metricName) {
        Counter counter = counters.computeIfAbsent(metricName,
                name -> meterRegistry.counter(name));
        counter.increment();
    }
}
