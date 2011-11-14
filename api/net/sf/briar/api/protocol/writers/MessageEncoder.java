package net.sf.briar.api.protocol.writers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import net.sf.briar.api.protocol.Author;
import net.sf.briar.api.protocol.Group;
import net.sf.briar.api.protocol.Message;
import net.sf.briar.api.protocol.MessageId;

public interface MessageEncoder {

	/** Encodes a private message. */
	Message encodeMessage(MessageId parent, String subject, byte[] body)
	throws IOException, GeneralSecurityException;

	/** Encodes an anonymous message to an unrestricted group. */
	Message encodeMessage(MessageId parent, Group group, String subject,
			byte[] body) throws IOException, GeneralSecurityException;

	/** Encodes an anonymous message to a restricted group. */
	Message encodeMessage(MessageId parent, Group group, PrivateKey groupKey,
			String subject, byte[] body) throws IOException,
			GeneralSecurityException;

	/** Encodes a pseudonymous message to an unrestricted group. */
	Message encodeMessage(MessageId parent, Group group, Author author,
			PrivateKey authorKey, String subject, byte[] body)
	throws IOException, GeneralSecurityException;

	/** Encode a pseudonymous message to a restricted group. */
	Message encodeMessage(MessageId parent, Group group, PrivateKey groupKey,
			Author author, PrivateKey authorKey, String subject, byte[] body)
	throws IOException, GeneralSecurityException;
}