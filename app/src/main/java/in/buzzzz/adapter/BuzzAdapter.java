package in.buzzzz.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import in.buzzzz.R;
import in.buzzzz.activity.BuzzzzDetailActivity;
import in.buzzzz.model.Interest;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.Logger;
import in.buzzzz.utility.Utility;

/**
 * Created by Rajendra Singh on 26/9/15.
 */
public class BuzzAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mActivity;
    private List<Interest> interestList;
    private LayoutInflater mLayoutInflater;

    public BuzzAdapter(Activity context, List<Interest> FollowingList) {
        this.mActivity = context;
        this.interestList = FollowingList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.interest_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            final Interest interest = interestList.get(position);
            viewHolder.textViewInterestName.setText(interest.getName());
            Logger.i("name", interest.getName());

            viewHolder.relativeLayoutFollowing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBuzzList(interest);
                }
            });
            viewHolder.imageViewSubscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subscribeInterest(interest);
                }
            });
        }
    }

    private void subscribeInterest(Interest interest) {
        Utility.showToastMessage(mActivity, interest.getName());
    }

    private void showBuzzList(Interest interest) {
        Utility.showToastMessage(mActivity, interest.getId());
        Intent intent = new Intent(mActivity, BuzzzzDetailActivity.class);
        intent.putExtra(AppConstants.EXTRA_BUZZZZ_ID, "560637acc830ec03dc42baae");
        intent.putExtra(AppConstants.EXTRA_BUZZZZ_NAME, interest.getName());
        mActivity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return (interestList == null) ? 0 : interestList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfilePic, imageViewSubscribe;
        TextView textViewInterestName;
        RelativeLayout relativeLayoutFollowing;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewProfilePic = (ImageView) itemView.findViewById(R.id.imageview_interest_pic);
            imageViewSubscribe = (ImageView) itemView.findViewById(R.id.imageview_subscribe);
            textViewInterestName = (TextView) itemView.findViewById(R.id.textview_interest_name);
            relativeLayoutFollowing = (RelativeLayout) itemView.findViewById(R.id.relativelayt_interest);
        }
    }
}
