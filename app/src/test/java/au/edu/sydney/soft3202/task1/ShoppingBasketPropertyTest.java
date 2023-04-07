package au.edu.sydney.soft3202.task1;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
public class ShoppingBasketPropertyTest {


    @Property
    void getValueEmptyPropertyTest(@ForAll @IntRange(min = 1, max = Integer.MAX_VALUE) int n) {
        ShoppingBasket sut = new ShoppingBasket();
        String name = "apple";
        sut.addItem(name, n);
        sut.removeItem(name, n);
        assertNull(sut.getValue());
    }
}
