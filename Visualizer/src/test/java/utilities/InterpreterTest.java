package utilities;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by jar156 on 11/05/17.
 */
public class InterpreterTest {
    Interpreter interpreter;

    @Before
    public void setUp() throws Exception {
        interpreter = new Interpreter();
    }

    @Test
    public void returnsFalseWhenInvalidHost() throws Exception {
        assertFalse(interpreter.receive("invalidhost", 4941));
    }

    @Test
    public void returnsFalseWhenInvalidPort() throws Exception {
        assertFalse(interpreter.receive("csse-s302staff.canterbury.ac.nz", 4));
        // TODO use local mock instead because this host won't always be running
    }

    @Test
    public void returnsTrueWhenConnectionSuccessful() throws Exception {
        assertTrue(interpreter.receive("csse-s302staff.canterbury.ac.nz", 4941));
    }

    @Test
    public void ignoresPacketWithUnknownMessageType() {
        byte unknownMessageType = 100;
        byte[] header = {unknownMessageType,0,0,0};
        byte[] packet = {0};

        interpreter.interpretPacket(header, packet);
    }

    @Test
    public void ignoresEmptyPacket() {
        byte[] header = {12,0,0,0};
        byte[] packet = {};

        interpreter.interpretPacket(header, packet);
        // TODO this (and similar) is really a RaceStatusParser test

    }

}