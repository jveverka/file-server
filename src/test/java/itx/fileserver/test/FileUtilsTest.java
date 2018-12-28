package itx.fileserver.test;

import itx.fileserver.services.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class FileUtilsTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "", "", true },
                { "c.txt", "*.txt", true },
                { "c.txt", "*.jpg", false },
                { "a/b/c.txt", "a/b/*", true },
                { "c.txt", "c.???", true },
                { "c.txt", "c.????", false },
                { "a/b/c.txt", "a/*/c.txt", true },
                { "a/b/c.txt", "a/*/x.txt", false },
                { "a.txt", "*", true },
                { "data/a.txt", "*", true },
                { "home/a.txt", "home/*", true },
                { "home/subdir/a.txt", "home/*", true },
                { "home/dir/subdir/a.txt", "home/*", true },
                { "home/dir/subdir/a.txt", "home/dir/subdir/a.txt", true },
                { "joe/data", "joe/*", true},
                { "joe/", "joe/*", true},
                { "joe/", "*", true},
                { "joe/data", "*", true},
                { "joe/data/file.txt", "*", true},
        });
    }

    private final String filename;
    private final String wildcardMatcher;
    private final boolean expectedResult;

    public FileUtilsTest(String filename, String wildcardMatcher, boolean expectedResult) {
        this.filename = filename;
        this.wildcardMatcher = wildcardMatcher;
        this.expectedResult = expectedResult;
    }

    @Test
    public void testMatcher() {
        boolean result = FileUtils.wildcardMatch(filename, wildcardMatcher);
        Assert.assertTrue(result == expectedResult);
    }

}
