package itx.examples.springboot.fileserver.test;

import itx.examples.springboot.fileserver.services.FileAccessServiceImpl;
import itx.examples.springboot.fileserver.services.dto.AccessType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RunWith(Parameterized.class)
public class MostRestrictiveAccessTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { createSet(AccessType.NONE), AccessType.NONE, false },
                { createSet(AccessType.READ), AccessType.NONE, false },
                { createSet(AccessType.READ_WRITE), AccessType.NONE, false },

                { createSet(AccessType.READ), AccessType.READ, true },
                { createSet(AccessType.READ, AccessType.READ_WRITE), AccessType.READ, true },
                { createSet(AccessType.READ_WRITE), AccessType.READ, true },
                { createSet(AccessType.NONE, AccessType.READ, AccessType.READ_WRITE), AccessType.READ, false },

                { createSet(AccessType.READ), AccessType.READ_WRITE, false },
                { createSet(AccessType.READ_WRITE, AccessType.READ), AccessType.READ_WRITE, false },
                { createSet(AccessType.NONE, AccessType.READ_WRITE, AccessType.READ), AccessType.READ_WRITE, false },
                { createSet(AccessType.READ_WRITE), AccessType.READ_WRITE, true },

        });
    }

    private static Set<AccessType> createSet(AccessType ... accessTypes) {
        Set<AccessType> result = new HashSet<>();
        for (AccessType accessType: accessTypes) {
            result.add(accessType);
        }
        return result;
    }

    private final Set<AccessType> accessTypes;
    private final AccessType actualAccessType;
    private final boolean expectedResult;

    public MostRestrictiveAccessTest(Set<AccessType> accessTypes, AccessType actualAccessType, boolean expectedResult) {
        this.accessTypes = accessTypes;
        this.actualAccessType = actualAccessType;
        this.expectedResult = expectedResult;
    }

    @Test
    public void accessTest() {
        boolean result = FileAccessServiceImpl.checkAccessUseMostRestrictive(accessTypes, actualAccessType);
        Assert.assertTrue(expectedResult == result);
    }

}
