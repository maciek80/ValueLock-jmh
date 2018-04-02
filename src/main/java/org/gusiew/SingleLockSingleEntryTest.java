package org.gusiew;

import org.gusiew.lock.api.Mutex;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class SingleLockSingleEntryTest extends AbstractSingleLockTest {

    @Benchmark
    public void doSynchronized(JvmLock jvmLock, Counter counter, Blackhole bh) {
        synchronized (jvmLock.lock) {
            bh.consume(counter.value++);
        }
    }

    @Benchmark
    public void doReentrantLock(RLock rLock, Counter counter, Blackhole bh) {
        rLock.lock.lock();
        try {
            bh.consume(counter.value++);
        } finally {
            rLock.lock.unlock();
        }
    }

    @Benchmark
    public void doValueLock(ValueLock vLock, Counter counter, Blackhole bh) {
        Mutex m = vLock.locker.lock(SAMPLE_VALUE);
        try {
            bh.consume(counter.value++);
        } finally {
            m.release();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SingleLockSingleEntryTest.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(10)
                .threads(8)
                .build();

        new Runner(opt).run();
    }
}
