package option;

import io.vavr.control.Option;
import org.junit.Test;

/**
 * Created by mtumilowicz on 2018-11-22.
 */
public class OptionInstanceMethodsTest {
    @Test
    public void x() {
//        Option.of("a").get();
//        Option.of("a").collect();
        Option.of("a").get();
//        Option.of("a").eq();
//        Option.of("a").filter();
//        Option.of("a").flatMap();
//        Option.of("a").getOrElse();
//        Option.of("a").getOrElseThrow();
//        Option.of("a").isLazy();
//        Option.of("a").isSingleValued();
//        Option.of("a").map();
//        Option.of("a").orElse();
//        Option.of("a").peek();         Option.of("a").onEmpty();
//        Option.of("a").transform();
//        Option.of("a").contains();
//        Option.of("a").corresponds();

//        Option.of("a").exists()
//        Option.of("a").forAll()
//        Option.of("a").forEach();
//        Option.of("a").getOrElseTry()
//        Option.of("a").getOrNull()
    
        
        Option.of(Option.of("a"))
//                .flatMap(x -> x)
                .stdout();
    }
}
