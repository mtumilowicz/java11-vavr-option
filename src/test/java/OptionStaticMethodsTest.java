import io.vavr.collection.Seq;
import io.vavr.control.Option;
import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
    }

    @Test
    public void some_null_instanceOf() {
        Option<Object> some = Option.some(null);

        assertTrue(some instanceof Option.Some);
    }
    
    @Test
    public void when_odd() {
        int x = 5;
        Option<Integer> when = Option.when(x % 2 == 0, x);

        assertTrue(when.isEmpty());
    }

    @Test
    public void when_even() {
        int x = 5;
        Option<Integer> when = Option.when(x % 2 == 1, x);

        assertTrue(when.isDefined());
    }
}
