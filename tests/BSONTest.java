import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import org.jai.BSON.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BSONTest extends TestCase {

    public static Test suite() {
        return new JUnit4TestAdapter(BSONTest.class);
    }

    private BSONDocument documentA;
    private BSONDocument documentB;
    private BSONDocument documentC;
    private BSONDocument documentE;

    public void setUp() {

        documentE = new BSONDocument();

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


        documentC = new BSONDocument();
        documentC.add("Binary", "data");
        ByteBuffer b = ByteBuffer.allocate(10);
        for (byte i = 0; i < 10; i++) {
            b.put(i);
        }
        documentC.add("Bytes", b);
    }

    public void tearDown() {
    }

    public void testDocumentGet() {
        assertEquals("Get string", documentA.get("A"), "hello");
        assertEquals("Get int", documentA.get("B"), 42);


        assertNotSame("Get string", documentA.get("A"), "LOL");
        assertNotSame("Get int", documentA.get("B"), 4);
    }

    public void testDocumentIsEmpty() {
        assertFalse("Document A is empty", documentA.isEmpty());
        assertTrue("Document E is not empty", documentE.isEmpty());
    }

    public void testDocumentSize() {
        assertEquals("Unexpected documentA size", 2, documentA.size());
        assertEquals("Unexpected documentB size", 2, documentB.size());
        assertEquals("Unexpected documentC size", 0, documentE.size());
    }

    public void testDocumentClear() {
        int sizeBeforeClearing = documentA.size();
        assertTrue("DocumentA does not contains element A", documentA.exist("A"));
        documentA.clear();
        assertTrue(sizeBeforeClearing != 0 && documentA.size() == 0);
        assertFalse("DocumentA contains element A", documentA.exist("A"));
    }

    public void testDocumentExists() {
        assertTrue("A does not exists", documentA.exist("A"));
        assertTrue("B does not exists", documentA.exist("B"));
        assertFalse("C does not exists", documentA.exist("C"));
    }

    public void testDocumentRemove() {
        assertTrue("A does not exists", documentA.exist("A"));
        assertTrue("B does not exists", documentA.exist("B"));
        documentA.remove("A");
        assertFalse("A is not removed", documentA.exist("A"));
        assertTrue("B does not exists", documentA.exist("B"));
    }

    public void testDocumentIterator() {
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

    public void testBSONEncoder() throws IOException {
        ByteBuffer buffer = BSONEncoder.encode(documentA);

        byte STRING = 0x02/*00*/;
        byte INT32 = 0x10/*00*/;
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

    public void testBSONDecoder() {
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


        buffer = BSONEncoder.encode(documentC);
        BSONDocument decodedDocumentC = BSONDecoder.decode(buffer);
        assertEquals("Unexpected document type", decodedDocumentC.get("Binary"), "data");

        ByteBuffer bytes = (ByteBuffer) decodedDocumentC.get("Bytes");
        for (byte i = 0; i < 10; i++) {
            assertEquals("Element #" + i + " has unexpected value", i, bytes.get());
        }
    }


    public void testBSONDocumentChainOperations() {
        documentA.clear().add("ParameterA", 3.14).add("ParameterB", "I will be removed").add("ParameterC", "42").remove("ParameterB");

        assertTrue("Unexpected document size", documentA.size() == 2);
        assertEquals("Element is corrupted", 3.14, documentA.get("ParameterA"));
        assertEquals("Element is corrupted", "42", documentA.get("ParameterC"));
    }

    public void testBSONArrayAdd() {
        BSONArray array = new BSONArray();

        String hello = "Hello,";
        String beauty = "Beauty";
        String world = "World!";

        array.add(hello).add(beauty).add(world);

        assertEquals("Unexpected value", hello, array.get("0"));
        assertEquals("Unexpected value", beauty, array.get("1"));
        assertEquals("Unexpected value", world, array.get("2"));
    }
}