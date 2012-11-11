package tests;

import BSON.BSONDecoder;
import BSON.BSONDocument;
import BSON.BSONDocumentElement;
import BSON.BSONEncoder;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static BSON.BSONElementTypes.INT32;
import static BSON.BSONElementTypes.STRING;

public class BSONTest extends TestCase {
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(BSONTest.class);
    }

    private BSONDocument documentA;
    private BSONDocument documentB;

    @Before
    public void setUp() {
        documentA = new BSONDocument();
        documentA.add("A", "hello");
        documentA.add("B", 42);

        documentB = new BSONDocument();
        documentB.add("Type", "Ships");

        BSONDocument ships = new BSONDocument();

        BSONDocument ship = new BSONDocument();
        ship.add("Type", "Interceptor");
        ship.add("Health", 100);
        ships.add("0", ship);

        ship = new BSONDocument();
        ship.add("Type", "Interceptor");
        ship.add("Health", 20);
        ships.add("1", ship);

        ship = new BSONDocument();
        ship.add("Type", "Cruiser");
        ship.add("Health", 542);
        ships.add("2", ship);

        documentB.add("Ships", ships);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGet() {
        assertEquals("Get string", documentA.get("A"), "hello");
        assertEquals("Get int", documentA.get("B"), 42);

        assertNotSame("Get string", documentA.get("A"), "LOL");
        assertNotSame("Get int", documentA.get("B"), 4);
    }

    @Test
    public void testExists() {
        assertTrue("A does not exists", documentA.exist("A"));
        assertTrue("B does not exists", documentA.exist("B"));
        assertFalse("C does not exists", documentA.exist("C"));
    }

    @Test
    public void testRemove() {
        assertTrue("A does not exists", documentA.exist("A"));
        assertTrue("B does not exists", documentA.exist("B"));
        documentA.remove("A");
        assertFalse("A is not removed", documentA.exist("A"));
        assertTrue("B does not exists", documentA.exist("B"));
    }

    @Test
    public void testIterrator() {
        for (BSONDocumentElement p : documentA) {
            assertTrue((p.getName().equals("A") || p.getName().equals("B")) && !p.getName().equals("C"));
        }
    }

    private String readString(ByteBuffer buffer) {
        ByteArrayOutputStream stringOStream = new ByteArrayOutputStream();
        byte temp;
        while ((temp = buffer.get()) != 0) {
            stringOStream.write(temp);
        }
        return stringOStream.toString();
    }

    @Test
    public void testBSONEncoder() throws IOException {
        ByteBuffer buffer = BSONEncoder.encode(documentA);

        assertEquals("Position is not 0", 0, buffer.position());
        assertEquals("Unexpected documentA size", 25, buffer.getInt());
        assertEquals("Unexpected element type", STRING, buffer.get());
        assertEquals("Unexpected name", "A", readString(buffer));
        assertEquals("Unexpected element size", 6, buffer.getInt());
        assertEquals("Unexpected value", "hello", readString(buffer));
        assertEquals("Unexpected element type", INT32, buffer.get());
        assertEquals("Invalid name", "B", readString(buffer));
        assertEquals("Unexpected value", 42, buffer.getInt());
        assertEquals("Unexpected byte", 0, buffer.get());
        assertEquals("Not at the end", 25, buffer.position());
    }

    @Test
    public void testBSON() {
        ByteBuffer buffer = BSONEncoder.encode(documentA);
        BSONDocument decodedDocumentA = BSONDecoder.decode(buffer);

        assertEquals("Get string", decodedDocumentA.get("A"), "hello");
        assertEquals("Get int", decodedDocumentA.get("B"), 42);

        assertNotSame("Get string", decodedDocumentA.get("A"), "LOL");
        assertNotSame("Get int", decodedDocumentA.get("B"), 4);

        buffer = BSONEncoder.encode(documentB);
        BSONDocument decodedDocumentB = BSONDecoder.decode(buffer);
        assertEquals("Unexpected document type", decodedDocumentB.get("Type"), "Ships");

        BSONDocument ships = (BSONDocument) decodedDocumentB.get("Ships");
        BSONDocument ship = (BSONDocument) ships.get("2");

        assertEquals("Unexpected ship type", ship.get("Type"), "Cruiser");
        assertEquals("Unexpected ship health", ship.get("Health"), 542);
    }
}