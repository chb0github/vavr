/*     / \____  _    ______   _____ / \____   ____  _____
 *    /  \__  \/ \  / \__  \ /  __//  \__  \ /    \/ __  \   Javaslang
 *  _/  // _\  \  \/  / _\  \\_  \/  // _\  \  /\  \__/  /   Copyright 2014-2015 Daniel Dietrich
 * /___/ \_____/\____/\_____/____/\___\_____/_/  \_/____/    Licensed under the Apache License, Version 2.0
 */
package javaslang.control;

import org.junit.Test;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

public class MatchMonadTest {

    // when(Object)

    @Test
    public void shouldMatchNullByValue() {
        final boolean actual = Match.of(null)
                .when(null).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldMatchIntByValue() {
        final boolean actual = Match.of(2)
                .when(1).then(false)
                .when(2).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    // whenIn(Onject...)

    @SuppressWarnings("unchecked")
    @Test
    public void shouldMatchNullByValueIn() {
        final boolean actual = Match.of(null)
                .whenIn((Object) null).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMatchNullVarargsByValueIn() {
        Match.of(null)
                .whenIn((Object[]) null).then(false)
                .get();
    }

    @Test
    public void shouldMatchIntByValueIn() {
        final boolean actual = Match.of(2)
                .whenIn(1, 2, 3).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldMatchOrElseByValueIn() {
        final boolean actual = Match.of(4)
                .whenIn(1, 2, 3).then(false)
                .orElse(true);
        assertThat(actual).isTrue();
    }

    // whenTrue(Predicate)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMatchNullVarargsByPredicate() {
        Match.of(null)
                .whenTrue(null).then(false)
                .get();
    }

    @Test
    public void shouldMatchEvenIntByPredicate() {
        final String divisibility = Match.of(0)
                .whenTrue((String s) -> true).then("oops")
                .whenTrue((Integer i) -> i % 2 == 0).then("even")
                .orElse("odd");
        assertThat(divisibility).isEqualTo("even");
    }

    @Test
    public void shouldMatchOddIntWithOrElseHavingUnmatchedPredicates() {
        final String divisibility = Match.of(1)
                .whenTrue((String s) -> true).then("oops")
                .whenTrue((Integer i) -> i % 2 == 0).then("even")
                .orElse("odd");
        assertThat(divisibility).isEqualTo("odd");
    }

    // whenType(Class)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMatchNullByType() {
        Match.of(null)
                .whenType(null).then(false)
                .get();
    }

    @Test
    public void shouldMatchNumberByType() {
        final String actual = Match.of(1)
                .whenType(String.class).then(s -> "String " + s)
                .whenType(Number.class).then(n -> "Number " + n)
                .whenType(Integer.class).then(i -> "int " + i)
                .orElse("unknown");
        assertThat(actual).isEqualTo("Number 1");
    }

    @Test
    public void shouldMatchOrElseByType() {
        final String actual = Match.of(1)
                .whenType(String.class).then(s -> "String " + s)
                .whenType(Short.class).then(s -> "Short " + s)
                .orElse("unknown");
        assertThat(actual).isEqualTo("unknown");
    }

    // whenTypeIn(Class...)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMatchNullVarargsByTypeIn() {
        Match.of(null)
                .whenTypeIn((Class<?>[]) null).then(false)
                .get();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldMatchNumberByTypeIn() {
        final Number number = 1;
        final String actual = Match.of(number)
                .whenTypeIn(Byte.class, Integer.class).then(s -> "matched")
                .orElse("unknown");
        assertThat(actual).isEqualTo("matched");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldMatchOrElseByTypeIn() {
        final Number number = 1;
        final String actual = Match.of(number)
                .whenTypeIn(Byte.class, Short.class).then(s -> "matched")
                .orElse("unknown");
        assertThat(actual).isEqualTo("unknown");
    }

    // whenApplicable(Function1)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMatchNullByFunction() {
        Match.of(null)
                .whenApplicable(null).thenApply()
                .get();
    }

    @Test
    public void shouldMatchIntByFunction() {
        final int actual = Match.of(1)
                .whenApplicable((Integer i) -> i + 1).thenApply()
                .get();
        assertThat(actual).isEqualTo(2);
    }

    @Test
    public void shouldMatchOrElseByFunction() {
        final boolean actual = Match.of(4)
                .whenIn(1, 2, 3).then(false)
                .orElse(true);
        assertThat(actual).isTrue();
    }

    // match by super-type

    @Test
    public void shouldMatchSuperTypeByValue() {
        final Option<Integer> option = Option.of(1);
        final boolean actual = Match.of(new Some<>(1))
                .when(option).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldMatchSuperTypeByValueIn() {
        final Option<Integer> option = Option.of(1);
        final boolean actual = Match.of(new Some<>(1))
                .whenIn(None.instance(), option).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldMatchSuperTypeByPredicate() {
        final boolean actual = Match.of(new Some<>(1))
                .whenTrue((Option<?> o) -> true).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldMatchSuperTypeByType() {
        final boolean actual = Match.of(new Some<>(1))
                .whenType(Option.class).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldMatchSuperTypeByTypeIn() {
        final boolean actual = Match.of(new Some<>(1))
                .whenTypeIn(Boolean.class, Option.class).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldMatchSuperTypeByFunction() {
        final boolean actual = Match.of(new Some<>(1))
                .whenApplicable((Option<?> o) -> true).thenApply()
                .get();
        assertThat(actual).isTrue();
    }

    // match by sub-type

    @Test
    public void shouldMatchSubTypeByValue() {
        final Option<Integer> option = Option.of(1);
        final boolean actual = Match.of(option)
                .when(new Some<>(1)).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldMatchSubTypeByValueIn() {
        final Option<Integer> option = Option.of(1);
        final boolean actual = Match.of(option)
                .whenIn(None.instance(), new Some<>(1)).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldMatchSubTypeByPredicate() {
        final Option<Integer> option = Option.of(1);
        final boolean actual = Match.of(option)
                .whenTrue((Some<?> o) -> true).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldMatchSubTypeByType() {
        final Option<Integer> option = Option.of(1);
        final boolean actual = Match.of(option)
                .whenType(Some.class).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldMatchSubTypeByTypeIn() {
        final Option<Integer> option = Option.of(1);
        final boolean actual = Match.of(option)
                .whenTypeIn(Boolean.class, Some.class).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldMatchSubTypeByFunction() {
        final Option<Integer> option = Option.of(1);
        final boolean actual = Match.of(option)
                .whenApplicable((Some<?> o) -> true).thenApply()
                .get();
        assertThat(actual).isTrue();
    }

    // should apply then() when matched (honoring When, WhenApplicable)

    @Test
    public void shouldApplyThenValueWhenMatched() {
        final boolean actual = Match.of(1).when(1).then(true).get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldApplyThenSupplierWhenMatched() {
        final boolean actual = Match.of(1).when(1).then(() -> true).get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldApplyThenFunctionWhenMatched() {
        final boolean actual = Match.of(true).when(true).then(b -> b).get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldApplyFunctionWhenApplicableMatched() {
        final boolean actual = Match.of(true).whenApplicable((Boolean b) -> b).thenApply().get();
        assertThat(actual).isTrue();
    }

    // should not apply then() when unmatched (honoring When, WhenApplicable)

    @Test
    public void shouldNotApplyThenValueWhenUnmatched() {
        final boolean actual = Match.of(0).when(1).then(false).orElse(true);
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldNotApplyThenSupplierWhenUnmatched() {
        final boolean actual = Match.of(0).when(1).then(() -> false).orElse(true);
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldNotApplyThenFunctionWhenUnmatched() {
        final boolean actual = Match.of(true).when(false).then(b -> b).orElse(true);
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldNotApplyFunctionWhenApplicableUnmatched() {
        final boolean actual = Match.of(true).whenApplicable((Integer i) -> false).thenApply().orElse(true);
        assertThat(actual).isTrue();
    }

    // otherwise() vs. orElse()

    @Test
    public void shouldUseOtherwiseToEnableMonadicOperationsWithMatched() {
        final double actual = Match.of(1)
                .whenType(Number.class).then(n -> n)
                .otherwise(() -> {
                    throw new UnsupportedOperationException("not numeric");
                })
                .map(Number::doubleValue)
                .get();
        assertThat(actual).isEqualTo(1.0d);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldUseOtherwiseToEnableMonadicOperationsWithUnmatched() {
        Match.of("1")
                .whenType(Number.class).then(n -> n)
                .otherwise(() -> {
                    throw new UnsupportedOperationException("not numeric");
                })
                .map(Number::doubleValue)
                .get();
    }

    @Test
    public void shouldUseOrElseToReturnAlternateValueWithMatched() {
        final double actual = Match.of(1)
                .whenType(Number.class).then(Number::doubleValue)
                .orElse(Double.NaN);
        assertThat(actual).isEqualTo(1.0d);
    }

    @Test
    public void shouldUseOrElseToReturnAlternateValueWithUnmatched() {
        final double actual = Match.of("1")
                .whenType(Number.class).then(Number::doubleValue)
                .orElse(Double.NaN);
        assertThat(actual).isNaN();
    }

    // transport matched states

    @Test
    public void shouldTransportMatchedValue() {
        final boolean actual = Match.of(1)
                .when(1).then(true)
                .when(1).then(false)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldTransportMatchedValueIn() {
        final boolean actual = Match.of(1)
                .whenIn(1).then(true)
                .whenIn(1).then(false)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldTransportMatchedPredicate() {
        final boolean actual = Match.of(1)
                .whenTrue((Integer i) -> true).then(true)
                .whenTrue((Integer i) -> true).then(false)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldTransportMatchedType() {
        final boolean actual = Match.of(1)
                .whenType(Integer.class).then(true)
                .whenType(Integer.class).then(false)
                .get();
        assertThat(actual).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldTransportMatchedTypeIn() {
        final boolean actual = Match.of(1)
                .whenTypeIn(Integer.class).then(true)
                .whenTypeIn(Integer.class).then(false)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldTransportMatchedFunction() {
        final boolean actual = Match.of(1)
                .whenApplicable((Integer i) -> true).thenApply()
                .whenApplicable((Integer i) -> false).thenApply()
                .get();
        assertThat(actual).isTrue();
    }

    // transport unmatched states

    @Test
    public void shouldTransportUnmatchedValue() {
        final boolean actual = Match.of(1)
                .when(0).then(false)
                .when(1).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldTransportUnmatchedValueIn() {
        final boolean actual = Match.of(1)
                .whenIn(0).then(false)
                .whenIn(1).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldTransportUnmatchedPredicate() {
        final boolean actual = Match.of(1)
                .whenTrue((Boolean b) -> false).then(false)
                .whenTrue((Integer i) -> true).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldTransportUnmatchedType() {
        final boolean actual = Match.of(1)
                .whenType(Boolean.class).then(false)
                .whenType(Integer.class).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldTransportUnmatchedTypeIn() {
        final boolean actual = Match.of(1)
                .whenTypeIn(Boolean.class).then(false)
                .whenTypeIn(Integer.class).then(true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldTransportUnmatchedFunction() {
        final boolean actual = Match.of(1)
                .whenApplicable((Boolean i) -> false).thenApply()
                .whenApplicable((Integer i) -> true).thenApply()
                .get();
        assertThat(actual).isTrue();
    }

    // should declare common result type of multiple cases

    @Test
    public void shouldMatchTypedResultByValue() {
        final Number actual = Match.of(1).as(Number.class)
                .when(0).then(0.0d)
                .when(1).then(1)
                .get();
        assertThat(actual).isEqualTo(1);
    }

    @Test(expected = MatchError.class)
    public void shouldMatchTypedResultByValueNegativeCase() {
        Match.of(-1).as(Number.class)
                .when(0).then(0.0d)
                .when(1).then(1)
                .get();
    }

    @Test
    public void shouldMatchTypedResultByValueIn() {
        final Number actual = Match.of(1).as(Number.class)
                .whenIn(0).then(0.0d)
                .whenIn(1).then(1)
                .get();
        assertThat(actual).isEqualTo(1);
    }

    @Test(expected = MatchError.class)
    public void shouldMatchTypedResultByValueInNegativeCase() {
        Match.of(-1).as(Number.class)
                .whenIn(0).then(0.0d)
                .whenIn(1).then(1)
                .get();
    }

    @Test
    public void shouldMatchTypedResultByPredicate() {
        final Number actual = Match.of(1).as(Number.class)
                .whenTrue((Double d) -> true).then(0.0d)
                .whenTrue((Integer i) -> true).then(1)
                .get();
        assertThat(actual).isEqualTo(1);
    }

    @Test(expected = MatchError.class)
    public void shouldMatchTypedResultByPredicateNegativeCase() {
        Match.of("x").as(Number.class)
                .whenTrue((Double d) -> true).then(0.0d)
                .whenTrue((Integer i) -> true).then(1)
                .get();
    }

    @Test
    public void shouldMatchTypedResultByType() {
        final Number actual = Match.of(1).as(Number.class)
                .whenType(Boolean.class).then(0.0d)
                .whenType(Integer.class).then(1)
                .get();
        assertThat(actual).isEqualTo(1);
    }

    @Test(expected = MatchError.class)
    public void shouldMatchTypedResultByTypeNegativeCase() {
        Match.of("x").as(Number.class)
                .whenType(Boolean.class).then(0.0d)
                .whenType(Integer.class).then(1)
                .get();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldMatchTypedResultByTypeIn() {
        final Number actual = Match.of(1).as(Number.class)
                .whenTypeIn(Boolean.class).then(0.0d)
                .whenTypeIn(Integer.class).then(1)
                .get();
        assertThat(actual).isEqualTo(1);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = MatchError.class)
    public void shouldMatchTypedResultByTypeInNegativeCase() {
        Match.of("x").as(Number.class)
                .whenTypeIn(Boolean.class).then(0.0d)
                .whenTypeIn(Integer.class).then(1)
                .get();
    }

    @Test
    public void shouldMatchTypedResultByFunction() {
        final Number actual = Match.of(1).as(Number.class)
                .whenApplicable((Double d) -> d).thenApply()
                .whenApplicable((Integer i) -> i).thenApply()
                .get();
        assertThat(actual).isEqualTo(1);
    }

    @Test(expected = MatchError.class)
    public void shouldMatchTypedResultByFunctionNegativeCase() {
        Match.of("x").as(Number.class)
                .whenApplicable((Double d) -> d).thenApply()
                .whenApplicable((Integer i) -> i).thenApply()
                .get();
    }

    // otherwise

    @Test
    public void shouldHandleOtherwiseValueOfMatched() {
        final boolean actual = Match.of(1)
                .when(1).then(true)
                .otherwise(false)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldHandleOtherwiseValueOfUnmatched() {
        final boolean actual = Match.of(1)
                .when(0).then(false)
                .otherwise(true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldHandleOtherwiseSupplierOfMatched() {
        final boolean actual = Match.of(1)
                .when(1).then(true)
                .otherwise(() -> false)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldHandleOtherwiseSupplierOfUnmatched() {
        final boolean actual = Match.of(1)
                .when(0).then(false)
                .otherwise(() -> true)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldHandleOtherwiseFunctionOfMatched() {
        final boolean actual = Match.of(1)
                .when(1).then(true)
                .otherwise(i -> i != 1)
                .get();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldHandleOtherwiseFunctionOfUnmatched() {
        final boolean actual = Match.of(1)
                .when(0).then(false)
                .otherwise(i -> i == 1)
                .get();
        assertThat(actual).isTrue();
    }

    // FilterMonadic operations

    @Test
    public void shouldFlatMapMatched() {
        final int actual = Match.of(1)
                .when(1).then(1)
                .flatMap(i -> Match.of(i).when(1).then(2))
                .get();
        assertThat(actual).isEqualTo(2);
    }

    @Test
    public void shouldFlatMapUnmatched() {
        final int actual = Match.of(0)
                .when(1).then(1)
                .flatMap(i -> Match.of(i).when(1).then(1))
                .orElse(-1);
        assertThat(actual).isEqualTo(-1);
    }

    @Test
    public void shouldFlatMapOtherwise() {
        final int actual = Match.of(0)
                .when(1).then(1)
                .otherwise(-1)
                .flatMap(i -> Match.of(i).when(-1).then(2))
                .get();
        assertThat(actual).isEqualTo(2);
    }

    @Test
    public void shouldFlatttenMatched() {
        final int actual = Match.of(1)
                .when(1).then(1)
                .flatten(i -> Match.of(i).when(1).then(2))
                .get();
        assertThat(actual).isEqualTo(2);
    }

    @Test
    public void shouldFlattenUnmatched() {
        final int actual = Match.of(0)
                .when(1).then(1)
                .flatten(i -> Match.of(i).when(1).then(1))
                .orElse(-1);
        assertThat(actual).isEqualTo(-1);
    }

    @Test
    public void shouldFlattenOtherwise() {
        final int actual = Match.of(0)
                .when(1).then(1)
                .otherwise(-1)
                .flatten(i -> Match.of(i).when(-1).then(2))
                .get();
        assertThat(actual).isEqualTo(2);
    }

    @Test
    public void shouldMapMatched() {
        final int actual = Match.of(1)
                .when(1).then(1)
                .map(i -> i + 1)
                .get();
        assertThat(actual).isEqualTo(2);
    }

    @Test
    public void shouldMapUnmatched() {
        final int actual = Match.of(0)
                .when(1).then(1)
                .map(i -> i + 1)
                .orElse(-1);
        assertThat(actual).isEqualTo(-1);
    }

    @Test
    public void shouldMapOtherwise() {
        final int actual = Match.of(0)
                .when(1).then(1)
                .otherwise(-1)
                .map(i -> i + 1)
                .get();
        assertThat(actual).isEqualTo(0);
    }

    // TraversableOnce operations

    @Test
    public void shouldGetIteratorOfMatched() {
        final Iterator<Integer> actual = Match.of(1)
                .when(1).then(1)
                .iterator();
        assertThat(actual.next()).isEqualTo(1);
        assertThat(actual.hasNext()).isFalse();
    }

    @Test
    public void shouldGetIteratorOfUnmatched() {
        final Iterator<Integer> actual = Match.of(0)
                .when(1).then(1)
                .iterator();
        assertThat(actual.hasNext()).isFalse();
    }

    @Test
    public void shouldGetIteratorOfOtherwise() {
        final Iterator<Integer> actual = Match.of(0)
                .when(1).then(1)
                .otherwise(-1)
                .iterator();
        assertThat(actual.next()).isEqualTo(-1);
        assertThat(actual.hasNext()).isFalse();
    }
}
