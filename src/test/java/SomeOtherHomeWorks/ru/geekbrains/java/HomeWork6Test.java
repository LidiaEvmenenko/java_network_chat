package SomeOtherHomeWorks.ru.geekbrains.java;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HomeWork6Test {
    public static HomeWork6 app = null;

    @BeforeAll
    public static void init() {
        app = new HomeWork6();
    }

    @Test
    public void testingTheExceptionOfNewArrAfterTheLast4Method() {
        int[] a = new int[]{1, 6, 3, 9, 8, 7, 9, 3, 2, 3};
        assertThrows(RuntimeException.class, () -> app.newArrAfterTheLast4(a));

    }

    private static Stream<Arguments> newArrAfterTheLast4Args() {
        return Stream.of(
                Arguments.of(new int[]{1, 6, 3, 9, 4, 7, 9, 4, 2, 6}, new int[]{2, 6}),
                Arguments.of(new int[]{1, 6, 3, 9, 4, 7, 9, 4, 2, 4}, new int[]{}),
                Arguments.of(new int[]{1, 4, 4, 7, 4, 7, 9, 3, 2, 3}, new int[]{7, 9, 3, 2, 3})
        );
    }

    @ParameterizedTest
    @MethodSource("newArrAfterTheLast4Args")
    public void testingTheNewArrAfterTheLast4Method(int[] a, int[] expected) {
        assertArrayEquals(expected, app.newArrAfterTheLast4(a));
    }

    private static Stream<Arguments> checkFor1Or4Args() {
        return Stream.of(
                Arguments.of(new int[]{1, 6, 3, 9, 4 ,7, 9 ,4, 2, 6}, true),
                Arguments.of(new int[]{0, 6, 3, 9, 4, 7, 9, 4, 2, 4}, true),
                Arguments.of(new int[]{1, 6, 3, 9, 8, 7, 9, 5, 2, 5}, true),
                Arguments.of(new int[]{6, 6, 3, 9, 3, 7, 9, 8, 2, 0}, false)
        );
    }

    @ParameterizedTest
    @MethodSource("checkFor1Or4Args")
    public void testingTheCheckFor1Or4Method(int[] a, boolean expected) {
        boolean b = app.checkFor1Or4(a);
        assertEquals(expected,b);
    }
}
