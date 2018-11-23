# java11-vavr-option
Overview of vavr Option API.

_Reference_: http://www.vavr.io/vavr-docs/#_option  
_Reference_: https://static.javadoc.io/io.vavr/vavr/0.9.2/io/vavr/control/Option.html  
_Reference_: https://softwaremill.com/do-we-have-better-option-here/  
_Reference_: http://blog.vavr.io/the-agonizing-death-of-an-astronaut/  

# preface
`Option` is a monadic container type which represents 
an optional value. Instances of `Option` are either an 
instance of `Option.Some` or the singleton `Option.None`.

If you’re coming to `Vavr` after using Java’s `Optional` class, 
there is a crucial difference. In `Optional`, a call to 
`.map` that results in a null will result in an empty 
`Optional`. In `Vavr`, it would result in a `Some(null)` that 
can then lead to a `NullPointerException`.

This seems like Vavr’s implementation is broken, but in 
fact it’s not - rather, it adheres to the requirement 
of a monad to maintain computational context when 
calling `.map`. In terms of an `Option`, this means that 
calling `.map` on a `Some` will result in a `Some`, and 
calling `.map` on a `None` will result in a `None`. In the 
Java `Optional` example above, that context changed from 
a `Some` to a `None`.

# project description
We provide description and tests of Option methods.

## static methods
* `Option<T>	narrow(Option<? extends T> option)` - 
Narrows a widened `Option<? extends T>` to `Option<T>` 
by performing a type-safe cast.
    ```
    Option<String> a = Option.of("a");
    Option<CharSequence> narrowed = Option.narrow(a);
    ```
    note that
    ```
    Option<String> a = null;
    assertNull(Option.narrow(a));
    ```
* `Option<T>	none()` - 
Returns the single instance of `None`
    ```
    assertSame(Option.none(), Option.none());
    ```
* `Option<T>	of(T value)` - 
Creates a new `Option` of a given value. 
`Some(value)` if value is not null, `None` otherwise
    ```
    Option<Object> option = Option.of("a");
    assertThat(option.get(), is("a"));
    ```
    ```
    Option<Object> option = Option.of(null);
    assertTrue(option.isEmpty());
    ```
* `Option<T>	ofOptional(Optional<? extends T> optional)` - 
Wraps a `Java Optional` to a new `Option`. 
`Some(optional.get())` if value is Java `Optional` 
is present, `None` otherwise.
    ```
    Option.ofOptional(null); // throws NPE
    ```
* `Option<Seq<T>>	sequence(Iterable<? extends Option<? extends T>> values)` - 
Reduces many `Options` into a single `Option` by transforming an `Iterable<Option<? extends T>>` into a `Option<Seq<T>>`.
    ```
    Option<Seq<String>> sequence = Option.sequence(List.of(Option.of("a"), Option.of("b")));
    assertTrue(sequence.get().eq(List.of("a", "b")));
    ```
    If any of the `Options` are `Option.None`, then this returns `Option.None`.
    ```
    Option<Seq<String>> sequence = Option.sequence(List.of(Option.of("a"), Option.of("b"), Option.none()));
    assertTrue(sequence.isEmpty());
    ```
* `Option<T>	some(T value)` - 
Creates a new `Some` of a given value.
    ```
    Option<Object> some = Option.some(null);
    assertTrue(some.isDefined());
    assertTrue(some instanceof Option.Some);
    assertNull(some.get());
    ```
    difference to `of()`:
    ```
    Option.of(null);   // = None
    Option.some(null); // = Some(null)
    ```
* `Option<T>	when(boolean condition, Supplier<? extends T> supplier)` - 
Creates Some of suppliers value if condition is true, or `None` in other case
* `Option<T>	when(boolean condition, T value)` - 
Creates `Some` of value if condition is true, or `None` in other case
    ```
    Option<Integer> when = Option.when(false, 1);
    assertTrue(when.isEmpty());
    ```
    ```
    Option<Integer> when = Option.when(true, 1);
    assertTrue(when.isDefined());
    ```
    ```
    Option<Integer> when = Option.when(true, (Integer) null);
    assertTrue(when.isDefined());
    ```