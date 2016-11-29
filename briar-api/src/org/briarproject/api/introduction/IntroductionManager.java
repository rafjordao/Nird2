package org.briarproject.api.introduction;

import org.briarproject.api.FormatException;
import org.briarproject.api.clients.SessionId;
import org.briarproject.api.contact.Contact;
import org.briarproject.api.contact.ContactId;
import org.briarproject.api.db.DbException;
import org.briarproject.api.messaging.ConversationManager.ConversationClient;
import org.briarproject.api.nullsafety.NotNullByDefault;
import org.briarproject.api.sync.ClientId;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@NotNullByDefault
public interface IntroductionManager extends ConversationClient {

	/** The unique ID of the introduction client. */
	ClientId CLIENT_ID = new ClientId("org.briarproject.briar.introduction");

	/**
	 * sends two initial introduction messages
	 */
	void makeIntroduction(Contact c1, Contact c2, @Nullable String msg,
			final long timestamp)
			throws DbException, FormatException;

	/**
	 * Accept an introduction that had been made
	 */
	void acceptIntroduction(final ContactId contactId,
			final SessionId sessionId, final long timestamp)
			throws DbException, FormatException;

	/**
	 * Decline an introduction that had been made
	 */
	void declineIntroduction(final ContactId contactId,
			final SessionId sessionId, final long timestamp)
			throws DbException, FormatException;

	/**
	 * Get all introduction messages for the contact with this contactId
	 */
	Collection<IntroductionMessage> getIntroductionMessages(ContactId contactId)
			throws DbException;

}
