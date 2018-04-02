package org.gusiew;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AbstractSingleLockTest extends AbstractLockTest {

    static final String SAMPLE_VALUE = "A";

    @State(Scope.Benchmark)
    public static class JvmLock {
        final Object lock = new Object();
    }

    @State(Scope.Benchmark)
    public static class RLock {
        final Lock lock = new ReentrantLock();
    }
}
