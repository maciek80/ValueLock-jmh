package org.gusiew;

import org.gusiew.lock.api.Mutex;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class SingleLockReentrancyTest extends AbstractSingleLockTest {

    //FIXME better values
    @Param({"8"/*, "4", "8"*/})
    private int size;

    //inne
    @Benchmark
    public void doSynchronized(JvmLock jvmLock, Counter counter, Blackhole bh) {
        internalDoSynchronize(size, jvmLock, counter, bh);
    }

    private void internalDoSynchronize(int depth, JvmLock jvmLock, Counter counter, Blackhole bh) {
        if (depth > 0) {
            synchronized (jvmLock.lock) {
                bh.consume(counter.value++);
                internalDoSynchronize(depth - 1, jvmLock, counter, bh);
                bh.consume(counter.value--);
            }
        }
    }

    @Benchmark
    public void doReentrantLock(RLock rLock, Counter counter, Blackhole bh) {
        internalDoReentrantLock(size, rLock, counter, bh);
    }

    private void internalDoReentrantLock(int depth, RLock rLock, Counter counter, Blackhole bh) {
        if (depth > 0) {
            rLock.lock.lock();
            try {
                bh.consume(counter.value++);
                internalDoReentrantLock(depth - 1, rLock, counter, bh);
                bh.consume(counter.value--);
            } finally {
                rLock.lock.unlock();
            }
        }
    }

    @Benchmark
    public void doValueLock(ValueLock vLock, Counter counter, Blackhole bh) {
        internalDoValueLock(size, vLock, counter, bh);
    }

    private void internalDoValueLock(int depth, ValueLock vLock, Counter counter, Blackhole bh) {
        if (depth > 0) {
            Mutex m = vLock.locker.lock(SAMPLE_VALUE);
            try {
                bh.consume(counter.value++);
                internalDoValueLock(depth - 1, vLock, counter, bh);
                bh.consume(counter.value--);
            } finally {
                m.release();
            }
        }
    }


    @TearDown
    public void check(Counter counter) {
        assert counter.value == 0  : "Race condition ?";
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SingleLockReentrancyTest.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(10)
                .threads(8)
                .build();

        new Runner(opt).run();
    }

}
