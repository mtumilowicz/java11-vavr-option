package option;

import io.vavr.PartialFunction;

import java.util.Objects;

import static java.util.Objects.isNull;

/**
 * Created by mtumilowicz on 2018-11-26.
 */
class PartialFunctionExample {
    static PartialFunction<String, Integer> stringLength = new PartialFunction<>() {

        @Override
        public Integer apply(String s) {
            return isNull(s) ? 0 : s.length();
        }

        @Override
        public boolean isDefinedAt(String value) {
            return Objects.equals("a", value);
        }
    };
}
