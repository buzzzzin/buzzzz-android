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
import in.buzzzz.activity.BuzzDetailActivity;
import in.buzzzz.model.BuzzPreview;
import in.buzzzz.utility.Api;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.Logger;
import in.buzzzz.utility.Utility;

/**
 * Created by Rajendra Singh on 26/9/15.
 */
public class BuzzAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mActivity;
    List<BuzzPreview> buzzPreviewList;

    private LayoutInflater mLayoutInflater;

    public BuzzAdapter(Activity context, List<BuzzPreview> FollowingList) {
        this.mActivity = context;
        this.buzzPreviewList = FollowingList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.buzz_preview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            final BuzzPreview buzzPreview = buzzPreviewList.get(position);
            viewHolder.textViewInterestName.setText(buzzPreview.getName());
            if (buzzPreview.getStats() != null && !buzzPreview.getStats().getGoingCount().equalsIgnoreCase("0")) {
                viewHolder.textViewSubscriberCount.setText("Going:" + buzzPreview.getStats().getGoingCount());

            } else {
                viewHolder.textViewSubscriberCount.setText("Awaiting response");

            }

            Utility.setImageFromUrl(Api.BASE_URL_CLOUDINARY_BUZZZZ + buzzPreview.getImageName(), viewHolder.imageViewProfilePic, R.mipmap.ic_launcher);
            Logger.i("name", buzzPreview.getName());

            viewHolder.relativeLayoutBuzz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBuzzList(buzzPreview);
                }
            });
            viewHolder.imageViewSubscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subscribeInterest(buzzPreview);
                }
            });
        }
    }

    private void subscribeInterest(BuzzPreview interest) {
        Utility.showToastMessage(mActivity, interest.getName());
    }

    private void showBuzzList(BuzzPreview interest) {
        Intent intent = new Intent(mActivity, BuzzDetailActivity.class);
        intent.putExtra(AppConstants.EXTRA_BUZZZZ_ID, interest.getBuzzId());
        intent.putExtra(AppConstants.EXTRA_BUZZZZ_NAME, interest.getName());
        mActivity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return (buzzPreviewList == null) ? 0 : buzzPreviewList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfilePic, imageViewSubscribe;
        TextView textViewInterestName, textViewSubscriberCount;
        RelativeLayout relativeLayoutBuzz;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewProfilePic = (ImageView) itemView.findViewById(R.id.imageview_interest_pic);
            imageViewSubscribe = (ImageView) itemView.findViewById(R.id.imageview_subscribe);
            textViewInterestName = (TextView) itemView.findViewById(R.id.textview_interest_name);
            textViewSubscriberCount = (TextView) itemView.findViewById(R.id.textview_subscriber_count);
            relativeLayoutBuzz = (RelativeLayout) itemView.findViewById(R.id.relativelayt_interest);
        }
    }
}
