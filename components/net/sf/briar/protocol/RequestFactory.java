package net.sf.briar.protocol;

import java.util.BitSet;

import net.sf.briar.api.protocol.OfferId;
import net.sf.briar.api.protocol.Request;

interface RequestFactory {

	Request createRequest(OfferId offerId, BitSet requested);
}
