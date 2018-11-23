package option;

import io.vavr.collection.Seq;
import io.vavr.control.Option;
import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * Created by mtumilowicz on 2018-11-22.
 */
public class OptionStaticMethodsTest {
    
    @Test
    public void narrow() {
        Option<String> a = Option.of("a");
        
        Option<CharSequence> narrowed = Option.narrow(a);
        
        assertThat(narrowed.get(), is("a"));
    }
    
    @Test
    public void narrow_null() {
        Option<String> a = null;

        assertNull(Option.narrow(a));
    }
    
    @Test(expected = NoSuchElementException.class)
    public void none_get() {
        Option.none().get();
    }

    @Test
    public void none_instanceof() {
        assertTrue(Option.none() instanceof Option.None);
    }
    
    @Test
    public void none_singleton() {
        assertSame(Option.none(), Option.none());
    }

    @Test
    public void of_notNull() {
        Option<Object> option = Option.of("a");

        assertThat(option.get(), is("a"));
    }
    
    @Test
    public void of_null() {
        Option<Object> option = Option.of(null);
        
        assertTrue(option.isEmpty());
    }
    
    @Test
    public void sequence_withNone() {
        Option<Seq<String>> sequence = Option.sequence(List.of(Option.of("a"), Option.of("b"), Option.none()));
        
        assertTrue(sequence.isEmpty());
    }

    @Test
    public void sequence_withoutNone() {
        Option<Seq<String>> sequence = Option.sequence(List.of(Option.of("a"), Option.of("b")));

        assertTrue(sequence.isDefined());
        assertTrue(sequence.get().eq(List.of("a", "b")));
    }
    
    @Test
    public void some_null_isDefined() {
        Option<Object> some = Option.some(null);

        assertTrue(some.isDefined());
        assertNull(some.get());
    }

    @Test
    public void some_null_instanceOf() {
        Option<Object> some = Option.some(null);

        assertTrue(some instanceof Option.Some);
    }
    
    @Test
    public void when_false() {
        Option<Integer> when = Option.when(false, 1);
        assertTrue(when.isEmpty());
    }

    @Test
    public void when_true() {
        Option<Integer> when = Option.when(true, 1);
        assertTrue(when.isDefined());
    }
    
    @Test
    public void when_null() {
        Option<Integer> when = Option.when(true, (Integer) null);
        assertTrue(when.isDefined());
    }
    
    @Test(expected = NullPointerException.class)
    public void ofOptional_null() {
        Option.ofOptional(null);
    }
}
