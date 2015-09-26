package in.buzzzz.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.buzzzz.R;
import in.buzzzz.model.ChatInfo;
import in.buzzzz.utility.Utility;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FIRST_TYPE = 1;
    private static final int SECOND_TYPE = 2;
    private Activity mActivity;
    private List<ChatInfo> mFollowingList;
    private LayoutInflater mLayoutInflater;

    public ChatAdapter(Activity context, List<ChatInfo> FollowingList) {
        this.mActivity = context;
        this.mFollowingList = FollowingList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View view;
        if (viewType == FIRST_TYPE) {
            view = mLayoutInflater.inflate(R.layout.sender_row, parent, false);
        } else {
            view = mLayoutInflater.inflate(R.layout.receiver_row, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            final ChatInfo chatInfo = mFollowingList.get(position);
            viewHolder.textViewName.setText(chatInfo.getSenderName());
            viewHolder.textViewMessage.setText(chatInfo.getMessage());
            Utility.setImageFromUrl(chatInfo.getImageUrl(), viewHolder.imageViewProfile, R.mipmap.ic_launcher);
        }
    }

    @Override
    public int getItemCount() {
        return (mFollowingList == null) ? 0 : mFollowingList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMessage, textViewName;
        private ImageView imageViewProfile;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewMessage = (TextView) itemView.findViewById(R.id.txtview_chat);
            textViewName = (TextView) itemView.findViewById(R.id.txtview_name);
            imageViewProfile = (ImageView) itemView.findViewById(R.id.imageview_profile_pic);
        }
    }

    public int getItemViewType(int position) {
        //Some logic to know which type will come next;

        if (mFollowingList.get(position).isOwnMessage()) {
            return FIRST_TYPE;
        } else {
            return SECOND_TYPE;
        }
    }
}
