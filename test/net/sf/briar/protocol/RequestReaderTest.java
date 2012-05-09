package net.sf.briar.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.BitSet;

import net.sf.briar.BriarTestCase;
import net.sf.briar.api.FormatException;
import net.sf.briar.api.protocol.PacketFactory;
import net.sf.briar.api.protocol.ProtocolConstants;
import net.sf.briar.api.protocol.Request;
import net.sf.briar.api.protocol.Types;
import net.sf.briar.api.serial.Reader;
import net.sf.briar.api.serial.ReaderFactory;
import net.sf.briar.api.serial.Writer;
import net.sf.briar.api.serial.WriterFactory;
import net.sf.briar.crypto.CryptoModule;
import net.sf.briar.serial.SerialModule;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class RequestReaderTest extends BriarTestCase {

	private final ReaderFactory readerFactory;
	private final WriterFactory writerFactory;
	private final PacketFactory packetFactory;
	private final Mockery context;

	public RequestReaderTest() throws Exception {
		super();
		Injector i = Guice.createInjector(new CryptoModule(),
				new ProtocolModule(), new SerialModule());
		readerFactory = i.getInstance(ReaderFactory.class);
		writerFactory = i.getInstance(WriterFactory.class);
		packetFactory = i.getInstance(PacketFactory.class);
		context = new Mockery();
	}

	@Test
	public void testFormatExceptionIfRequestIsTooLarge() throws Exception {
		PacketFactory packetFactory = context.mock(PacketFactory.class);
		RequestReader requestReader = new RequestReader(packetFactory);

		byte[] b = createRequest(true);
		ByteArrayInputStream in = new ByteArrayInputStream(b);
		Reader reader = readerFactory.createReader(in);
		reader.addStructReader(Types.REQUEST, requestReader);

		try {
			reader.readStruct(Types.REQUEST, Request.class);
			fail();
		} catch(FormatException expected) {}
		context.assertIsSatisfied();
	}

	@Test
	public void testNoFormatExceptionIfRequestIsMaximumSize() throws Exception {
		final PacketFactory packetFactory = context.mock(PacketFactory.class);
		RequestReader requestReader = new RequestReader(packetFactory);
		final Request request = context.mock(Request.class);
		context.checking(new Expectations() {{
			oneOf(packetFactory).createRequest(with(any(BitSet.class)),
					with(any(int.class)));
			will(returnValue(request));
		}});

		byte[] b = createRequest(false);
		ByteArrayInputStream in = new ByteArrayInputStream(b);
		Reader reader = readerFactory.createReader(in);
		reader.addStructReader(Types.REQUEST, requestReader);

		assertEquals(request, reader.readStruct(Types.REQUEST,
				Request.class));
		context.assertIsSatisfied();
	}

	@Test
	public void testBitmapDecoding() throws Exception {
		// Test sizes from 0 to 1000 bits
		for(int i = 0; i < 1000; i++) {
			// Create a BitSet of size i with one in ten bits set (on average)
			BitSet requested = new BitSet(i);
			for(int j = 0; j < i; j++) if(Math.random() < 0.1) requested.set(j);
			// Encode the BitSet as a bitmap
			int bytes = i % 8 == 0 ? i / 8 : i / 8 + 1;
			byte[] bitmap = new byte[bytes];
			for(int j = 0; j < i; j++) {
				if(requested.get(j)) {
					int offset = j / 8;
					byte bit = (byte) (128 >> j % 8);
					bitmap[offset] |= bit;
				}
			}
			// Create a serialised request containing the bitmap
			byte[] b = createRequest(bitmap);
			// Deserialise the request
			ByteArrayInputStream in = new ByteArrayInputStream(b);
			Reader reader = readerFactory.createReader(in);
			RequestReader requestReader = new RequestReader(packetFactory);
			reader.addStructReader(Types.REQUEST, requestReader);
			Request r = reader.readStruct(Types.REQUEST, Request.class);
			BitSet decoded = r.getBitmap();
			// Check that the decoded BitSet matches the original - we can't
			// use equals() because of padding, but the first i bits should
			// match and the cardinalities should be equal, indicating that no
			// padding bits are set
			for(int j = 0; j < i; j++) {
				assertEquals(requested.get(j), decoded.get(j));
			}
			assertEquals(requested.cardinality(), decoded.cardinality());
		}
	}

	private byte[] createRequest(boolean tooBig) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = writerFactory.createWriter(out);
		w.writeStructId(Types.REQUEST);
		// Allow one byte for the REQUEST tag, one byte for the padding length
		// as a uint7, one byte for the BYTES tag, and five bytes for the
		// length of the byte array as an int32
		int size = ProtocolConstants.MAX_PACKET_LENGTH - 8;
		if(tooBig) size++;
		assertTrue(size > Short.MAX_VALUE);
		w.writeUint7((byte) 0);
		w.writeBytes(new byte[size]);
		assertEquals(tooBig, out.size() > ProtocolConstants.MAX_PACKET_LENGTH);
		return out.toByteArray();
	}

	private byte[] createRequest(byte[] bitmap) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Writer w = writerFactory.createWriter(out);
		w.writeStructId(Types.REQUEST);
		w.writeUint7((byte) 0);
		w.writeBytes(bitmap);
		return out.toByteArray();
	}
}
