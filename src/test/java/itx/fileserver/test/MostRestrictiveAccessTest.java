package itx.fileserver.test;

import itx.fileserver.services.FileAccessServiceImpl;
import itx.fileserver.dto.AccessType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MostRestrictiveAccessTest {

    public static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of( createSet(AccessType.NONE), AccessType.NONE, false ),
                Arguments.of( createSet(AccessType.READ), AccessType.NONE, false ),
                Arguments.of( createSet(AccessType.READ_WRITE), AccessType.NONE, false ),

                Arguments.of( createSet(AccessType.READ), AccessType.READ, true ),
                Arguments.of( createSet(AccessType.READ, AccessType.READ_WRITE), AccessType.READ, true ),
                Arguments.of( createSet(AccessType.READ_WRITE), AccessType.READ, true ),
                Arguments.of( createSet(AccessType.NONE, AccessType.READ, AccessType.READ_WRITE), AccessType.READ, false ),

                Arguments.of( createSet(AccessType.READ), AccessType.READ_WRITE, false ),
                Arguments.of( createSet(AccessType.READ_WRITE, AccessType.READ), AccessType.READ_WRITE, false ),
                Arguments.of( createSet(AccessType.NONE, AccessType.READ_WRITE, AccessType.READ), AccessType.READ_WRITE, false ),
                Arguments.of( createSet(AccessType.READ_WRITE), AccessType.READ_WRITE, true )
        );
    }

    private static Set<AccessType> createSet(AccessType ... accessTypes) {
        Set<AccessType> result = new HashSet<>();
        for (AccessType accessType: accessTypes) {
            result.add(accessType);
        }
        return result;
    }

    @ParameterizedTest
    @MethodSource("data")
    void accessTest(Set<AccessType> accessTypes, AccessType actualAccessType, boolean expectedResult) {
        boolean result = FileAccessServiceImpl.checkAccessUseMostRestrictive(accessTypes, actualAccessType);
        assertEquals(expectedResult, result);
    }

}
