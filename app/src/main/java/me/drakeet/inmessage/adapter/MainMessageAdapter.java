package me.drakeet.inmessage.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.drakeet.inmessage.R;
import me.drakeet.inmessage.api.OnItemClickListener;
import me.drakeet.inmessage.model.Message;
import me.drakeet.inmessage.utils.SmsUtils;
import me.drakeet.inmessage.utils.TaskUtils;

/**
 * Created by shengkun on 15/6/5.
 */
public class MainMessageAdapter extends RecyclerView.Adapter<MainMessageAdapter.ViewHolder> {

    public static final int ITEM_TYPE_DATE = 0;
    public static final int ITEM_TYPE_MESSAGE = 1;

    private LayoutInflater mInflater;

    private List<Message> mList;
    private SmsUtils mSmsUtils;
    private Boolean mShowResult = false;

    private OnItemClickListener listener;

    public MainMessageAdapter(Context context, List<Message> messageList) {
        mInflater = LayoutInflater.from(context);
        mList = messageList;
        mSmsUtils = new SmsUtils(context);
    }

    @Override
    public MainMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_DATE:
                return new DateViewHolder(mInflater.inflate(R.layout.view_separation, parent, false));
            case ITEM_TYPE_MESSAGE:
                return new MessageViewHolder(mInflater.inflate(R.layout.item_message, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(MainMessageAdapter.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ITEM_TYPE_DATE:
                onBindDateViewHolder((DateViewHolder) holder, position);
                break;
            case ITEM_TYPE_MESSAGE:
                onBindMessageViewHolder((MessageViewHolder) holder, position);
                break;
        }
    }

    private void onBindDateViewHolder(DateViewHolder holder, int position) {
        holder.dateTv.setText(mList.get(position).getReceiveDate());
    }

    private void onBindMessageViewHolder(MessageViewHolder holder, int position) {
        holder.authorTv.setText(mList.get(position).getSender());

        if (mShowResult && mList.get(position).getResultContent() != null) {
            holder.contentTv.setText(mList.get(position).getResultContent());
        }
        else {
            holder.contentTv.setText(mList.get(position).getContent());
        }

        if (mList.get(position).getReceiveDate() != null) {
            holder.dateTv.setText(mList.get(position).getReceiveDate());
        }

        holder.authorTv.setText(mList.get(position).getSender());
        holder.avatarTv.setText(getFormattedCompanyName(position));
    }

    private String getFormattedCompanyName(int position) {
        String showCompanyName = mList.get(position).getCompanyName();

        if (showCompanyName == null) {
            return "?";
        }

        if (showCompanyName.length() == 4) {
            String fourCharsName = "";
            for (int u = 0; u < showCompanyName.length();u ++) {
                if (u == 2) {
                    fourCharsName += "\n";
                }
                fourCharsName += showCompanyName.charAt(u);
            }
            showCompanyName = fourCharsName;
        }

        return showCompanyName;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void getAvatar(final String phoneNumber, final ImageView imageView, final TextView textView, final Message message) {
        TaskUtils.executeAsyncTask(
                new AsyncTask<Object, Object, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Object... params) {
                        Bitmap bitmap = mSmsUtils.getPeopleImage(phoneNumber);
                        return bitmap;
                    }

                    @Override
                    protected void onPostExecute(Bitmap o) {
                        super.onPostExecute(o);
                        if (o != null) {
                            imageView.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.GONE);
                            imageView.setImageBitmap(o);
                        } else {
                            textView.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.GONE);
                        }
                    }
                }
        );
    }

    private void getName(final String phoneNumber, final TextView textView, final Message message) {
        TaskUtils.executeAsyncTask(new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                return mSmsUtils.getContactNameFromPhoneBook(phoneNumber);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    textView.setText(s);
                    message.setAuthor(s);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getIsMessage() ? ITEM_TYPE_MESSAGE : ITEM_TYPE_DATE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

    }

    public class DateViewHolder extends ViewHolder {

        public TextView dateTv;

        public DateViewHolder(View itemView) {
            super(itemView);
            dateTv = (TextView) itemView.findViewById(R.id.date_message_tv);
        }

    }

    public class MessageViewHolder extends ViewHolder {

        public TextView authorTv;
        public TextView contentTv;
        public TextView avatarTv;
        public TextView dateTv;

        public MessageViewHolder(View itemView) {
            super(itemView);

            authorTv = (TextView) itemView.findViewById(R.id.author_message_tv);
            contentTv = (TextView) itemView.findViewById(R.id.content_message_tv);
            avatarTv = (TextView) itemView.findViewById(R.id.avatar_tv);
            dateTv = (TextView) itemView.findViewById(R.id.message_date_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(v, getAdapterPosition());
                }
            });
        }

    }

    private void onItemClick(View itemView, int position) {
        if (listener != null) {
            listener.onItemClick(itemView, position);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setShowResult(boolean showResult) {
        this.mShowResult = showResult;
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getIsMessage()) {
                notifyItemChanged(i);
            }
        }
    }

}
