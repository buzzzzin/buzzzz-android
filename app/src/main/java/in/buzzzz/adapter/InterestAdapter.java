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
import in.buzzzz.model.Interest;

/**
 * Created by Rajendra Singh on 26/9/15.
 */
public class InterestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FIRST_TYPE = 1;
    private static final int SECOND_TYPE = 2;
    private Activity mActivity;
    private List<Interest> mFollowingList;
    private LayoutInflater mLayoutInflater;

    public InterestAdapter(Activity context, List<Interest> FollowingList) {
        this.mActivity = context;
        this.mFollowingList = FollowingList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view;
        view = mLayoutInflater.inflate(R.layout.interest_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            final Interest chatInfo = mFollowingList.get(position);
            viewHolder.textViewName.setText(chatInfo.getName());


        }
    }


    @Override
    public int getItemCount() {
        return (mFollowingList == null) ? 0 : mFollowingList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private ImageView imageViewProfile, imageViewSubscribe;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.textview_interest_name);
            imageViewProfile = (ImageView) itemView.findViewById(R.id.imageview_interest_pic);
            imageViewSubscribe = (ImageView) itemView.findViewById(R.id.imageview_subscribe);

        }
    }

}
