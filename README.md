[![Build Status](https://travis-ci.com/mtumilowicz/java11-vavr-option.svg?branch=master)](https://travis-ci.com/mtumilowicz/java11-vavr-option)

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

## controversies
There are two functions that operate on Monads, 
`map` and `flatMap`. These obey specific laws. 
For this project it is sufficient to say that map has to 
preserve the computational context of a value. For 
context changes `flatMap` is used.

The constructor `Option.of(value)` puts a value into a 
computational context. If the value is `null`, the context 
is `None`, otherwise the context is `Some`.

Preserving this context means:
* `Some(value).map(v -> otherValue) ~ Some(otherValue)`
* `None().map(v -> otherValue) ~ None()`

**We do not expect context changes when mapping values. 
It would break the Monad laws.**

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
Creates `Some` of suppliers value if condition is true, or `None` in other case
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
## instance
* `Option<R>	collect(PartialFunction<? super T,? extends R> partialFunction)` - 
Collects value that is in the domain of the given 
`partialFunction` by mapping the value to type `R`.
* `boolean	equals(Object o)` - if `Some` call `equals` on values
    ```
    @Override
    public boolean equals(Object obj) {
        return (obj == this) || (obj instanceof Some && Objects.equals(value, ((Some<?>) obj).value));
    }
    ```
* `Option<T>	filter(Predicate<? super T> predicate)`
    ```
    default Option<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return isEmpty() || predicate.test(get()) ? this : none();
    }
    ```
    * `assertThat(Option.none().filter(x -> true), is(Option.none()));`
    * `assertThat(Option.some(1).filter(x -> false), is(Option.none()));`
    * `assertThat(Option.some(1).filter(x -> true), is(Option.of(1)));`
* `Option<U>	flatMap(Function<? super T,? extends Option<? extends U>> mapper)`
    ```
    default <U> Option<U> flatMap(Function<? super T, ? extends Option<? extends U>> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return isEmpty() ? none() : (Option<U>) mapper.apply(get());
    }
    ```
    * `assertThat(Option.of(Option.of(1)).flatMap(Function.identity()), is(Option.of(1)));`
    * `assertThat(Option.of(Option.some(null)).flatMap(Function.identity()), is(Option.some(null)));`
    * `assertThat(Option.none().flatMap(x -> null), is(Option.none()));`
    * `assertNull(Option.of(1).flatMap(x -> null));`
    * **difference to java `Optional` flatMap**: `Optional.of(1).flatMap(x -> null);` throws NPE
* `T	get()` - `NoSuchElementException` if `this` is a `None`.
    * `assertNull(Option.some(null).get());`
* `T	getOrElse(Supplier<? extends T> supplier)`
* `T	getOrElse(T other)`
* `<X extends Throwable> getOrElseThrow(Supplier<X> exceptionSupplier)`
* `boolean	isDefined()`
    * `assertTrue(Option.some(null).isDefined());`
* `boolean	isEmpty()`
* `Option<U>	map(Function<? super T,? extends U> mapper)`
   ```
   Option<U> map(Function<? super T, ? extends U> mapper) {
           Objects.requireNonNull(mapper, "mapper is null");
           return isEmpty() ? none() : some(mapper.apply(get()));
       }
   ```
   * `assertThat(Option.some(null).map(Function.identity()), is(Option.some(null)));`
   * `assertThat(Option.of(1).map(x -> null), is(Option.some(null)));`
   * `assertThat(Option.of(1).map(Function.identity()), is(Option.some(1)));`
   * `assertThat(Option.<Integer>none().map(Function.identity()), is(Option.none()));`
   * **difference to java `Optional` map**: `assertThat(Optional.of(1).map(x -> null), is(Optional.empty()));`
* `Option<T>	onEmpty(Runnable action)` - Runs a Java 
`Runnable` passed as parameter if this `Option` is empty.
* `Option<T>	orElse(Option<? extends T> other)`
* `Option<T>	orElse(Supplier<? extends Option<? extends T>> supplier)`
    * `assertThat(Repository.findById(1).orElse(Repository.findByName("Michal")), is(Option.of("found-by-id")));`
    * `assertThat(Repository.findById(1).orElse(Repository.findByName("Michal")), is(Option.of("found-by-name")));`
    where `Repository`:
    ```
    static Option<String> findById(int id) {
        return Option.when(id == 1, "found-by-id");
    }
    static Option<String> findByName(String name) {
        return Option.when(Objects.equals(name, "Michal"), "found-by-name");
    }
    ```
* `Option<T>	peek(Consumer<? super T> action)` - applies 
an action to this value, if this option is defined, 
otherwise does nothing. It is useful to construct `if-else`
(`ifPresentOrElse` in `Optional API`)
statements:
    ```
    AtomicBoolean invokedPeek = new AtomicBoolean();
    AtomicBoolean invokedOnEmpty = new AtomicBoolean();
    
    Option.of(1).peek(x -> invokedPeek.set(true))
            .onEmpty(() -> invokedOnEmpty.set(true));
    
    assertTrue(invokedPeek.get());
    assertFalse(invokedOnEmpty.get());
    ```
    ```
    AtomicBoolean invokedPeek = new AtomicBoolean();
    AtomicBoolean invokedOnEmpty = new AtomicBoolean();
    
    Option.none().peek(x -> invokedPeek.set(true))
            .onEmpty(() -> invokedOnEmpty.set(true));
    
    assertFalse(invokedPeek.get());
    assertTrue(invokedOnEmpty.get());
    ```
    ```
    AtomicBoolean invokedPeek = new AtomicBoolean();
    
    Integer value = Option.of(1).peek(x -> invokedPeek.set(true))
            .getOrElse(-1);
    
    assertTrue(invokedPeek.get());
    assertThat(value, is(1));
    ```
    ```
    AtomicBoolean invokedPeek = new AtomicBoolean();
    
    Integer value = Option.<Integer>none().peek(x -> invokedPeek.set(true))
            .getOrElse(-1);
    
    assertFalse(invokedPeek.get());
    assertThat(value, is(-1));
    ```
* `U	transform(Function<? super Option<T>,? extends U> f)`
    ```
    U transform(Function<? super Option<T>, ? extends U> f) {
            Objects.requireNonNull(f, "f is null");
            return f.apply(this);
        }
    ```
    it is generalized `map` - instead of operating directly 
    on values we have function `? super Option<T> -> U`.