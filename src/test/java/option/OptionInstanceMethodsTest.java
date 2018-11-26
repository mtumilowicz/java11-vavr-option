package option;

import io.vavr.PartialFunction;
import io.vavr.control.Option;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mtumilowicz on 2018-11-22.
 */
public class OptionInstanceMethodsTest {

    @Test
    public void collect_isDefined() {
        PartialFunction<String, Integer> length = new PartialFunction<>() {

            @Override
            public Integer apply(String s) {
                return isNull(s) ? 0 : s.length();
            }

            @Override
            public boolean isDefinedAt(String value) {
                return true;
            }
        };

        assertThat(Option.of("a").collect(length), is(Option.some(1)));
    }

    @Test
    public void collect_isNotDefined() {
        PartialFunction<String, Integer> length = new PartialFunction<>() {

            @Override
            public Integer apply(String s) {
                return isNull(s) ? 0 : s.length();
            }

            @Override
            public boolean isDefinedAt(String value) {
                return false;
            }
        };

        assertThat(Option.of("a").collect(length), is(Option.none()));
    }

    @Test
    public void equals_none() {
        assertSame(Option.none(), Option.none());
    }

    @Test
    public void equals_some_same_values() {
        assertThat(Option.of("a"), is(Option.of("a")));
    }

    @Test
    public void equals_some_different_values() {
        assertThat(Option.of("a"), is(not((Option.of("b")))));
    }

    @Test
    public void equals_when_values_have_bad_equals_method() {
        assertThat(Option.of(new BadEqualsOwner()), is(not((Option.of(new BadEqualsOwner())))));
    }

    @Test
    public void filter_none() {
        assertThat(Option.none().filter(x -> true), is(Option.none()));
    }

    @Test
    public void filter_some_not_fulfil() {
        assertThat(Option.some(1).filter(x -> false), is(Option.none()));
    }

    @Test
    public void filter_some_fulfil() {
        assertThat(Option.some(1).filter(x -> true), is(Option.of(1)));
    }

    @Test
    public void flatMap_some() {
        assertThat(Option.of(Option.of(1)).flatMap(Function.identity()), is(Option.of(1)));
    }

    @Test
    public void flatMap_some_null() {
        assertThat(Option.of(Option.some(null)).flatMap(Function.identity()), is(Option.some(null)));
    }

    @Test
    public void flatMap_none_function_returns_null() {
        assertThat(Option.none().flatMap(x -> null), is(Option.none()));
    }

    @Test
    public void flatMap_some_function_returns_null() {
        assertNull(Option.of(1).flatMap(x -> null));
    }

    @Test(expected = NullPointerException.class)
    public void optional_flatMap_some_function_returns_null() {
        Optional.of(1).flatMap(x -> null);
    }

    @Test
    public void get_some_null() {
        assertNull(Option.some(null).get());
    }

    @Test(expected = NoSuchElementException.class)
    public void get_none() {
        assertNull(Option.none().get());
    }

    @Test
    public void isDefined_some_null() {
        assertTrue(Option.some(null).isDefined());
    }

    @Test
    public void map_some_null() {
        assertThat(Option.some(null).map(Function.identity()), is(Option.some(null)));
    }

    @Test
    public void map_some_function_returns_null() {
        assertThat(Option.of(1).map(x -> null), is(Option.some(null)));
    }

    @Test
    public void optional_map_some_function_returns_null() {
        assertThat(Optional.of(1).map(x -> null), is(Optional.empty()));
    }

    @Test
    public void map_some() {
        assertThat(Option.of(1).map(Function.identity()), is(Option.some(1)));
    }

    @Test
    public void map_none() {
        assertThat(Option.<Integer>none().map(Function.identity()), is(Option.none()));
    }

    @Test
    public void peek_onEmpty_isDefined() {
        AtomicBoolean invokedPeek = new AtomicBoolean();
        AtomicBoolean invokedOnEmpty = new AtomicBoolean();

        Option.of(1).peek(x -> invokedPeek.set(true))
                .onEmpty(() -> invokedOnEmpty.set(true));

        assertTrue(invokedPeek.get());
        assertFalse(invokedOnEmpty.get());
    }

    @Test
    public void peek_onEmpty_isEmpty() {
        AtomicBoolean invokedPeek = new AtomicBoolean();
        AtomicBoolean invokedOnEmpty = new AtomicBoolean();

        Option.none().peek(x -> invokedPeek.set(true))
                .onEmpty(() -> invokedOnEmpty.set(true));

        assertFalse(invokedPeek.get());
        assertTrue(invokedOnEmpty.get());
    }

    @Test
    public void peek_orElse_isDefined() {
        AtomicBoolean invokedPeek = new AtomicBoolean();

        Integer value = Option.of(1).peek(x -> invokedPeek.set(true))
                .getOrElse(-1);

        assertTrue(invokedPeek.get());
        assertThat(value, is(1));
    }

    @Test
    public void peek_orElse_isEmpty() {
        AtomicBoolean invokedPeek = new AtomicBoolean();

        Integer value = Option.<Integer>none().peek(x -> invokedPeek.set(true))
                .getOrElse(-1);

        assertFalse(invokedPeek.get());
        assertThat(value, is(-1));
    }
}