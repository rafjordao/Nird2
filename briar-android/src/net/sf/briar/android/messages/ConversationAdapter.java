package net.sf.briar.android.messages;

import static android.graphics.Typeface.BOLD;
import static android.view.Gravity.CENTER_VERTICAL;
import static android.widget.LinearLayout.HORIZONTAL;
import static java.text.DateFormat.SHORT;

import java.util.ArrayList;

import net.sf.briar.R;
import net.sf.briar.android.widgets.CommonLayoutParams;
import net.sf.briar.api.db.PrivateMessageHeader;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

class ConversationAdapter extends ArrayAdapter<PrivateMessageHeader> {

	ConversationAdapter(Context ctx) {
		super(ctx, android.R.layout.simple_expandable_list_item_1,
				new ArrayList<PrivateMessageHeader>());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PrivateMessageHeader item = getItem(position);
		Context ctx = getContext();
		LinearLayout layout = new LinearLayout(ctx);
		layout.setOrientation(HORIZONTAL);
		layout.setGravity(CENTER_VERTICAL);

		ImageView star = new ImageView(ctx);
		star.setPadding(5, 5, 5, 5);
		if(item.isStarred()) star.setImageResource(R.drawable.rating_important);
		else star.setImageResource(R.drawable.rating_not_important);
		layout.addView(star);

		if(!item.getContentType().equals("text/plain")) {
			ImageView attachment = new ImageView(ctx);
			attachment.setPadding(0, 5, 5, 5);
			attachment.setImageResource(R.drawable.content_attachment);
			layout.addView(attachment);
		}

		TextView subject = new TextView(ctx);
		// Give me all the unused width
		subject.setLayoutParams(CommonLayoutParams.WRAP_WRAP_1);
		subject.setTextSize(14);
		subject.setMaxLines(2);
		if(!item.isRead()) subject.setTypeface(null, BOLD);
		subject.setText(item.getSubject());
		layout.addView(subject);

		TextView date = new TextView(ctx);
		date.setTextSize(14);
		date.setPadding(10, 0, 10, 0);
		long then = item.getTimestamp(), now = System.currentTimeMillis();
		date.setText(DateUtils.formatSameDayTime(then, now, SHORT, SHORT));
		layout.addView(date);

		return layout;
	}
}