package minborg.piproject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatternsTest {

    @Test
    void patternFor50() {
        long patter50 = Patterns.patternFor(0.5f);
        int bits = Long.bitCount(patter50);
        assertEquals(0.5f, (float) bits / Long.SIZE, 1e-9);
    }
}