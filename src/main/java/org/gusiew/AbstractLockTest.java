package org.gusiew;

import org.gusiew.lock.api.Locker;
import org.gusiew.lock.impl.ReentrantLocker;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AbstractLockTest {

    @State(Scope.Benchmark)
    public static class Counter {
        long value;
    }

    @State(Scope.Benchmark)
    public static class ValueLock {
        final Locker locker = new ReentrantLocker();
    }
}
