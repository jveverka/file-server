package itx.fileserver.test;

import itx.fileserver.services.FileUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


class FileUtilsTest {

    public static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of( "", "", true ),
                Arguments.of( "c.txt", "*.txt", true ),
                Arguments.of( "c.txt", "*.jpg", false ),
                Arguments.of( "a/b/c.txt", "a/b/*", true ),
                Arguments.of( "c.txt", "c.???", true ),
                Arguments.of( "c.txt", "c.????", false ),
                Arguments.of( "a/b/c.txt", "a/*/c.txt", true ),
                Arguments.of( "a/b/c.txt", "a/*/x.txt", false ),
                Arguments.of( "a.txt", "*", true ),
                Arguments.of( "data/a.txt", "*", true ),
                Arguments.of( "home/a.txt", "home/*", true ),
                Arguments.of( "home/subdir/a.txt", "home/*", true ),
                Arguments.of( "home/dir/subdir/a.txt", "home/*", true ),
                Arguments.of( "home/dir/subdir/a.txt", "home/dir/subdir/a.txt", true ),
                Arguments.of( "joe/data", "joe/*", true ),
                Arguments.of( "joe/", "joe/*", true ),
                Arguments.of( "joe/", "*", true ),
                Arguments.of( "joe/data", "*", true ),
                Arguments.of( "joe/data/file.txt", "*", true )
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void testMatcher(String filename, String wildcardMatcher, boolean expectedResult) {
        boolean result = FileUtils.wildcardMatch(filename, wildcardMatcher);
        assertEquals(expectedResult, result);
    }

}
