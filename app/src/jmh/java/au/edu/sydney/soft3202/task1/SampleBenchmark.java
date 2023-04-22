//package au.edu.sydney.soft3202.task1;
//
//import org.openjdk.jmh.annotations.*;
//import org.openjdk.jmh.infra.Blackhole;
//import org.openjdk.jmh.runner.Runner;
//import org.openjdk.jmh.runner.RunnerException;
//import org.openjdk.jmh.runner.options.Options;
//import org.openjdk.jmh.runner.options.OptionsBuilder;
//
//import java.sql.SQLException;
//
//@State(Scope.Benchmark)
//public class SampleBenchmark {
//
//    private ShoppingBasket sut;
//    private String itemForRemovePurpose = "itemForRemovePurpose";
//    private String itemForRemovePurpose2 = "itemForRemovePurpose2";
//
//    @Setup
//    public void setUp() throws SQLException {
//        sut = new ShoppingBasket();
//        sut.addNewItem(itemForRemovePurpose, 10);
//        sut.addNewItem(itemForRemovePurpose2, 10);
//
//    }
//
//    // new
//    @Fork(value=1)
//    @Warmup(iterations=1)
//    @Measurement(iterations = 1)
//    @Benchmark @BenchmarkMode(Mode.Throughput)
//    public void addItemBenchmark(Blackhole bh) {
//        sut.addNewItem("newItem", 10);
//    }
//
//    // existed
//    @Fork(value=1)
//    @Warmup(iterations=1)
//    @Measurement(iterations = 1)
//    @Benchmark @BenchmarkMode(Mode.Throughput)
//    public void removeBenchmark(Blackhole bh) {
//        sut.deleteItem("apple");
//    }
//
//    // add new item"s"
//    @Fork(value=1)
//    @Warmup(iterations=1)
//    @Measurement(iterations = 1)
//    @Benchmark @BenchmarkMode(Mode.Throughput)
//    public void addNewItems(Blackhole bh) {
//        sut.addNewItem("newItem", 10);
//        sut.addNewItem("newItem1", 10);
//        sut.addNewItem("newItem2", 10);
//    }
//
//    // remove new item"s"
//    @Fork(value=1)
//    @Warmup(iterations=1)
//    @Measurement(iterations = 1)
//    @Benchmark @BenchmarkMode(Mode.Throughput)
//    public void removeNewItems(Blackhole bh) {
//        sut.deleteItem(itemForRemovePurpose);
//        sut.deleteItem(itemForRemovePurpose2);
//    }
//
//    // add "a" new item, increase 10 and remove "that"
//    @Fork(value=1)
//    @Warmup(iterations=1)
//    @Measurement(iterations = 1)
//    @Benchmark @BenchmarkMode(Mode.Throughput)
//    public void addingNewItemNameAndIncrement(Blackhole bh) {
//        sut.addNewItem("newItem", 10);
//
//        for (int i = 0; i < 10; i ++) {
//            sut.addNewItem("newItem", 1);
//        }
//
//        for (int i = 0; i < 10; i ++) {
//            sut.removeItem("newItem", 1);
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        org.openjdk.jmh.Main.main(args);
//    }
//}
