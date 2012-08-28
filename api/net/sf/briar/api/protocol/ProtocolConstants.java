package net.sf.briar.api.protocol;

import static net.sf.briar.api.transport.TransportConstants.MIN_CONNECTION_LENGTH;

public interface ProtocolConstants {

	/**
	 * The maximum length of a serialised packet in bytes. To allow for future
	 * changes in the protocol, this is smaller than the minimum connection
	 * length minus the maximum encryption and authentication overhead.
	 */
	static final int MAX_PACKET_LENGTH = MIN_CONNECTION_LENGTH / 2;

	/** The maximum number of transports a node may support. */
	static final int MAX_TRANSPORTS = 25;

	/** The maximum number of properties per transport. */
	static final int MAX_PROPERTIES_PER_TRANSPORT = 100;

	/** The maximum length of a property's key or value in UTF-8 bytes. */
	static final int MAX_PROPERTY_LENGTH = 100;

	/** The maximum length of a group's name in UTF-8 bytes. */
	static final int MAX_GROUP_NAME_LENGTH = 50;

	/** The maximum length of a public key in bytes. */
	static final int MAX_PUBLIC_KEY_LENGTH = 120;

	/** The maximum length of an author's name in UTF-8 bytes. */
	static final int MAX_AUTHOR_NAME_LENGTH = 50;

	/**
	 * The maximum length of a message body in bytes. To allow for future
	 * changes in the protocol, this is smaller than the maximum packet length
	 * even when all the message's other fields have their maximum lengths.
	 */
	static final int MAX_BODY_LENGTH = MAX_PACKET_LENGTH - 1024;

	/** The maximum length of a message's subject line in UTF-8 bytes. */
	static final int MAX_SUBJECT_LENGTH = 100;

	/** The maximum length of a signature in bytes. */
	static final int MAX_SIGNATURE_LENGTH = 120;

	/** The length of a message's random salt in bytes. */
	static final int SALT_LENGTH = 8;
}
