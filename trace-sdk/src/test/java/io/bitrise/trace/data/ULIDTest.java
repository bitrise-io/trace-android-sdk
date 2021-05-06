package io.bitrise.trace.data;

import androidx.annotation.NonNull;

import org.junit.Test;

import io.azam.ulidj.ULID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests that verifies that the used ULID passes the ULID requirements and acts the same way on each platform.
 * <p>
 * The Android SDK uses {@link io.azam.ulidj.ULID}.
 *
 * @see <a href="https://github.com/ulid/spec">https://github.com/ulid/spec</a>
 */
public class ULIDTest {

    /**
     * Tests that the length of the ULID should be 26 chars.
     */
    @Test
    public void length_shouldBe26Characters() {
        final int actualValue = ULID.random().length();
        assertThat(actualValue, equalTo(26));
    }

    /**
     * Tests that 01BX5ZZKBKACTAV9WEVGEMMVRA is a valid ULID.
     */
    @Test
    public void isValid_shouldBeValid() {
        final String validULID = "01BX5ZZKBKACTAV9WEVGEMMVRA";
        final boolean actualValue = ULID.isValid(validULID);
        assertThat(actualValue, is(true));
    }

    /**
     * Asserts that the provided String ulid is NOT valid.
     * @param ulid the String to validate.
     */
    private void assertUlidIsNotValid(@NonNull final String ulid) {
        assertThat(ULID.isValid(ulid), is(false));
    }

    /**
     * Tests that ULIDs with letter "I" should be invalid.
     */
    @Test
    public void isValid_shouldNotContainI() {
        assertUlidIsNotValid("01BX5ZZKBKACTAV9WEVGEMMVRI");
    }

    /**
     * Tests that ULIDs with letter "L" should be invalid.
     */
    @Test
    public void isValid_shouldNotContainL() {
        assertUlidIsNotValid("01BX5ZZKBKACTAV9WEVGEMMVRL");
    }

    /**
     * Tests that ULIDs with letter "O" should be invalid.
     */
    @Test
    public void isValid_shouldNotContainO() {
        assertUlidIsNotValid("01BX5ZZKBKACTAV9WEVGEMMVRO");
    }

    /**
     * Tests that ULIDs with letter "U" should be invalid.
     */
    @Test
    public void isValid_shouldNotContainU() {
        assertUlidIsNotValid("01BX5ZZKBKACTAV9WEVGEMMVRU");
    }

    /**
     * Reads a timestamp of the ULID from the past.
     * <p>
     * Epoch timestamp: 128412198500
     * Date and time (GMT): Friday, 10 September 2010 12:33:05
     */
    @Test
    public void getTimStamp_shouldBeTheRequiredValue1() {
        final String validULID = "015BXT4ZZ8000G00R40M300209";
        final long actualValue = ULID.getTimestamp(validULID);
        assertThat(actualValue, equalTo(1284121985000L));
    }

    /**
     * Reads a timestamp of the ULID from the future.
     * <p>
     * Epoch timestamp: 1915273985000
     * Date and time (GMT): Tuesday, 10 September 2030 12:33:05
     */
    @Test
    public void getTimStamp_shouldBeTheRequiredValue2() {
        final String validULID = "01QQQKKEZ8000G00R40M300209";
        final long actualValue = ULID.getTimestamp(validULID);
        assertThat(actualValue, equalTo(1915273985000L));
    }
}
