package VotazioneFotografica.Model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ControlFunctionTest {

    private ControlFunction controlFunction;
    private Path tempStatusFile;
    private Path tempLockFile;

    @BeforeEach
    void setUp() throws IOException {
        tempStatusFile = Files.createTempFile("status", ".txt");
        tempLockFile = Files.createTempFile("lock", ".txt");
        
        controlFunction = new ControlFunction();
        ReflectionTestUtils.setField(controlFunction, "statusFilePath", tempStatusFile.toString());
        ReflectionTestUtils.setField(controlFunction, "lockFilePath", tempLockFile.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempStatusFile);
        Files.deleteIfExists(tempLockFile);
    }

    @Test
    void testInitWithNoFiles() throws IOException {
        Files.deleteIfExists(tempStatusFile);
        Files.deleteIfExists(tempLockFile);
        
        controlFunction.init();
        
        assertEquals(0, controlFunction.getControl());
        assertFalse(controlFunction.isControl2());
        assertTrue(Files.exists(tempStatusFile));
        assertTrue(Files.exists(tempLockFile));
    }

    @Test
    void testSetAndGetControl() throws IOException {
        controlFunction.init();
        
        controlFunction.setControl(1);
        assertEquals(1, controlFunction.getControl());
        assertEquals("1", Files.readString(tempStatusFile).trim());
        
        controlFunction.setControl(2);
        assertEquals(2, controlFunction.getControl());
        assertEquals("2", Files.readString(tempStatusFile).trim());

        controlFunction.setControl(0);
        assertEquals(0, controlFunction.getControl());
        assertEquals("0", Files.readString(tempStatusFile).trim());
    }

    @Test
    void testInvalidFileContent() throws IOException {
        Files.writeString(tempStatusFile, "invalid");
        controlFunction.init();
        assertEquals(0, controlFunction.getControl());
    }
}
