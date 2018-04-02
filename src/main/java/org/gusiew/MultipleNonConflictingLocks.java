package org.gusiew;

import org.gusiew.lock.api.Mutex;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
public class MultipleNonConflictingLocks extends AbstractLockTest {

    @State(Scope.Benchmark)
    public static class Locks {
        final ReentrantLock[] values = (ReentrantLock[])IntStream.rangeClosed(1, 4).boxed().map(i -> new ReentrantLock()).toArray(ReentrantLock[]::new);
    }

    @Benchmark
    @Group("synchronized")
    @GroupThreads
    public void doSynchronized1(Locks locks, Counter counter, Blackhole bh) {
        internalDoSynchronized(0, locks, counter, bh);
    }

    @Benchmark
    @Group("synchronized")
    @GroupThreads
    public void doSynchronized2(Locks locks, Counter counter, Blackhole bh) {
        internalDoSynchronized(1, locks, counter, bh);
    }

    @Benchmark
    @Group("synchronized")
    @GroupThreads
    public void doSynchronized3(Locks locks, Counter counter, Blackhole bh) {
        internalDoSynchronized(2, locks, counter, bh);
    }

    @Benchmark
    @Group("synchronized")
    @GroupThreads
    public void doSynchronized4(Locks locks, Counter counter, Blackhole bh) {
        internalDoSynchronized(3, locks, counter, bh);
    }

    private void internalDoSynchronized(int index, Locks locks, Counter counter, Blackhole bh) {
        synchronized (locks.values[index]) {
            bh.consume(counter.value++);
        }
    }

    @Benchmark
    @Group("reentrantLock")
    @GroupThreads
    public void doReentrantLock1(Locks locks, Counter counter, Blackhole bh) {
        internalDoReentrantLock(0, locks, counter, bh);
    }

    @Benchmark
    @Group("reentrantLock")
    @GroupThreads
    public void doReentrantLock2(Locks locks, Counter counter, Blackhole bh) {
        internalDoReentrantLock(1, locks, counter, bh);
    }

    @Benchmark
    @Group("reentrantLock")
    @GroupThreads
    public void doReentrantLock3(Locks locks, Counter counter, Blackhole bh) {
        internalDoReentrantLock(2, locks, counter, bh);
    }

    @Benchmark
    @Group("reentrantLock")
    @GroupThreads
    public void doReentrantLock4(Locks locks, Counter counter, Blackhole bh) {
        internalDoReentrantLock(3, locks, counter, bh);
    }

    private void internalDoReentrantLock(int index, Locks locks, Counter counter, Blackhole bh) {
        ReentrantLock lock = locks.values[index];
        lock.lock();
        try {
            bh.consume(counter.value++);
        } finally {
            lock.unlock();
        }
    }

    @Benchmark
    @Group("valueLock")
    @GroupThreads
    public void doValueLock1(ValueLock valueLock, Locks locks, Counter counter, Blackhole bh) {
        internalDoValueLock(0, valueLock, locks, counter, bh);
    }

    @Benchmark
    @Group("valueLock")
    @GroupThreads
    public void doValueLock2(ValueLock valueLock, Locks locks, Counter counter, Blackhole bh) {
        internalDoValueLock(1, valueLock, locks, counter, bh);
    }

    @Benchmark
    @Group("valueLock")
    @GroupThreads
    public void doValueLock3(ValueLock valueLock, Locks locks, Counter counter, Blackhole bh) {
        internalDoValueLock(2, valueLock, locks, counter, bh);
    }

    @Benchmark
    @Group("valueLock")
    @GroupThreads
    public void doValueLock4(ValueLock valueLock, Locks locks, Counter counter, Blackhole bh) {
        internalDoValueLock(3, valueLock, locks, counter, bh);
    }

    private void internalDoValueLock(int index, ValueLock valueLock, Locks locks, Counter counter, Blackhole bh) {
        Mutex m = valueLock.locker.lock(locks.values[index]);
        try {
            bh.consume(counter.value++);
        } finally {
            m.release();
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MultipleNonConflictingLocks.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(10)
                .threads(4)
                .build();

        new Runner(opt).run();
    }
}
