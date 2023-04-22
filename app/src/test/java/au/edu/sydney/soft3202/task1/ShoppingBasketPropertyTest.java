//package au.edu.sydney.soft3202.task1;
//
//import net.jqwik.api.*;
//import net.jqwik.api.constraints.IntRange;
//import net.jqwik.api.constraints.NotBlank;
//import net.jqwik.api.constraints.NotEmpty;
//import net.jqwik.api.constraints.StringLength;
//
//import java.sql.SQLException;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//public class ShoppingBasketPropertyTest {
//
//
//    @Provide
//    Arbitrary<String> items() {
//        return Arbitraries.of("apple", "orange", "pear", "banana");
//    }
//
//    @Property
//    void getValuePropertyTest(@ForAll  @IntRange(min = 1, max = Integer.MAX_VALUE) int n,
//                              @ForAll @From("items") String item) throws SQLException {
//        ShoppingBasket sut = new ShoppingBasket();
//        double price = 5;
//        sut.addNewItem(item, price);
//        sut.addItem(item, n);
//        assertEquals(price * n, sut.getValue());
//    }
//
//    @Property
//    void getValueEmptyPropertyTest(@ForAll  @IntRange(min = 1, max = Integer.MAX_VALUE) int n,
//                                   @ForAll @From("items") String item) throws SQLException {
//        ShoppingBasket sut = new ShoppingBasket();
//        double price = 5;
//        sut.addNewItem(item, price);
//        sut.addItem(item, n);
//        sut.removeItem(item, n);
//        assertNull(sut.getValue());
//    }
//
//    @Property
//    void getValueEmptyPropertyTestClear(@ForAll  @IntRange(min = 1, max = Integer.MAX_VALUE) int n,
//                                        @ForAll @From("items") String item) throws SQLException {
//        ShoppingBasket sut = new ShoppingBasket();
//        double price = 5;
//        sut.addNewItem(item, price);
//        sut.addItem(item, n);
//        sut.clear();
//        assertNull(sut.getValue());
//    }
//}
