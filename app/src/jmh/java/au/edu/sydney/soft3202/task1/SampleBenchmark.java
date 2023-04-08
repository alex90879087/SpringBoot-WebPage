package au.edu.sydney.soft3202.task1;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
public class SampleBenchmark {

    private ShoppingBasket sut;

    @Setup
    public void setUp() {
        sut = new ShoppingBasket();
    }

    // new
    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void addItemBenchmark(Blackhole bh) {
        sut.addNewItem("newItem", 10);
    }

    // existed
    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void removeBenchmark(Blackhole bh) {
        sut.deleteItem("apple");
    }

    // add new item"s"
    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void addNewItems(Blackhole bh) {
        sut.addNewItem("newItem", 10);
        sut.addNewItem("newItem1", 10);
        sut.addNewItem("newItem2", 10);
    }

    // remove new item"s"
    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void removeNewItems(Blackhole bh) {
        sut.addNewItem("newItem", 10);
        sut.addNewItem("newItem2", 10);
        sut.addNewItem("newItem2", 10);

        sut.deleteItem("newItem");
        sut.deleteItem("newItem1");
        sut.deleteItem("newItem3");
    }

    // add "a" new item, increase 10 and remove "that"
    @Fork(value=1)
    @Warmup(iterations=1)
    @Measurement(iterations = 1)
    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void addingNewItemNameAndIncrement(Blackhole bh) {
        sut.addNewItem("newItem", 10);

        for (int i = 0; i < 10; i ++) {
            sut.addNewItem("newItem", 1);
        }

        for (int i = 0; i < 10; i ++) {
            sut.removeItem("newItem", 1);
        }
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
