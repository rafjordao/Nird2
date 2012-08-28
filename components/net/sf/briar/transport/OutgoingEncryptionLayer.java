package net.sf.briar.transport;

import static javax.crypto.Cipher.ENCRYPT_MODE;
import static net.sf.briar.api.transport.TransportConstants.AAD_LENGTH;
import static net.sf.briar.api.transport.TransportConstants.HEADER_LENGTH;
import static net.sf.briar.api.transport.TransportConstants.IV_LENGTH;
import static net.sf.briar.api.transport.TransportConstants.MAC_LENGTH;
import static net.sf.briar.api.transport.TransportConstants.TAG_LENGTH;
import static net.sf.briar.util.ByteUtils.MAX_32_BIT_UNSIGNED;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;

import net.sf.briar.api.crypto.AuthenticatedCipher;
import net.sf.briar.api.crypto.ErasableKey;

class OutgoingEncryptionLayer implements FrameWriter {

	private final OutputStream out;
	private final Cipher tagCipher;
	private final AuthenticatedCipher frameCipher;
	private final ErasableKey tagKey, frameKey;
	private final byte[] iv, aad, ciphertext;
	private final int frameLength;

	private long capacity, frameNumber;
	private boolean writeTag;

	/** Constructor for the initiator's side of a connection. */
	OutgoingEncryptionLayer(OutputStream out, long capacity, Cipher tagCipher,
			AuthenticatedCipher frameCipher, ErasableKey tagKey,
			ErasableKey frameKey, int frameLength) {
		this.out = out;
		this.capacity = capacity;
		this.tagCipher = tagCipher;
		this.frameCipher = frameCipher;
		this.tagKey = tagKey;
		this.frameKey = frameKey;
		this.frameLength = frameLength;
		iv = new byte[IV_LENGTH];
		aad = new byte[AAD_LENGTH];
		ciphertext = new byte[frameLength];
		frameNumber = 0L;
		writeTag = true;
	}

	/** Constructor for the responder's side of a connection. */
	OutgoingEncryptionLayer(OutputStream out, long capacity,
			AuthenticatedCipher frameCipher, ErasableKey frameKey,
			int frameLength) {
		this.out = out;
		this.capacity = capacity;
		this.frameCipher = frameCipher;
		this.frameKey = frameKey;
		this.frameLength = frameLength;
		tagCipher = null;
		tagKey = null;
		iv = new byte[IV_LENGTH];
		aad = new byte[AAD_LENGTH];
		ciphertext = new byte[frameLength];
		frameNumber = 0L;
		writeTag = false;
	}

	public void writeFrame(byte[] frame, int payloadLength, boolean finalFrame)
			throws IOException {
		// If the initiator's side of the connection is closed without writing
		// any data, don't write anything to the underlying transport
		if(writeTag && finalFrame && payloadLength == 0) return;
		// Write the tag if required
		if(writeTag) {
			TagEncoder.encodeTag(ciphertext, tagCipher, tagKey);
			try {
				out.write(ciphertext, 0, TAG_LENGTH);
			} catch(IOException e) {
				frameKey.erase();
				tagKey.erase();
				throw e;
			}
			capacity -= TAG_LENGTH;
			writeTag = false;
		}
		// Encode the header
		FrameEncoder.encodeHeader(frame, finalFrame, payloadLength);
		// Don't pad the final frame
		int plaintextLength, ciphertextLength;
		if(finalFrame) {
			plaintextLength = HEADER_LENGTH + payloadLength;
			ciphertextLength = plaintextLength + MAC_LENGTH;
		} else {
			plaintextLength = frameLength - MAC_LENGTH;
			ciphertextLength = frameLength;
		}
		// If there's any padding it must all be zeroes
		for(int i = HEADER_LENGTH + payloadLength; i < plaintextLength; i++)
			frame[i] = 0;
		// Encrypt and authenticate the frame
		FrameEncoder.encodeIv(iv, frameNumber);
		FrameEncoder.encodeAad(aad, frameNumber, plaintextLength);
		try {
			frameCipher.init(ENCRYPT_MODE, frameKey, iv, aad);
			int encrypted = frameCipher.doFinal(frame, 0, plaintextLength,
					ciphertext, 0);
			if(encrypted != ciphertextLength) throw new RuntimeException();
		} catch(GeneralSecurityException badCipher) {
			throw new RuntimeException(badCipher);
		}
		// Write the frame
		try {
			out.write(ciphertext, 0, ciphertextLength);
		} catch(IOException e) {
			frameKey.erase();
			tagKey.erase();
			throw e;
		}
		capacity -= ciphertextLength;
		frameNumber++;
	}

	public void flush() throws IOException {
		out.flush();
	}

	public long getRemainingCapacity() {
		long capacityExcludingTag = writeTag ? capacity - TAG_LENGTH : capacity;
		long frames = capacityExcludingTag / frameLength;
		long frameNumbers = MAX_32_BIT_UNSIGNED - frameNumber + 1;
		int maxPayloadLength = frameLength - HEADER_LENGTH - MAC_LENGTH;
		return maxPayloadLength * Math.min(frames, frameNumbers);
	}
}