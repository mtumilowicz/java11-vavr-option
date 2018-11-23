package option;

import io.vavr.PartialFunction;
import io.vavr.control.Option;
import org.junit.Test;

import static java.util.Objects.isNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
}
