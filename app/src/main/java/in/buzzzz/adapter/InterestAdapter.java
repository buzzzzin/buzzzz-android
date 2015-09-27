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

import java.util.HashMap;
import java.util.List;

import in.buzzzz.R;
import in.buzzzz.activity.InterestActivity;
import in.buzzzz.activity.BuzzListActivity;
import in.buzzzz.loader.APICaller;
import in.buzzzz.loader.LoaderCallback;
import in.buzzzz.model.Interest;
import in.buzzzz.model.Model;
import in.buzzzz.model.Request;
import in.buzzzz.parser.MessageParser;
import in.buzzzz.utility.Api;
import in.buzzzz.utility.ApiDetails;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.Logger;
import in.buzzzz.utility.SharedPreference;
import in.buzzzz.utility.Utility;

/**
 * Created by Rajendra Singh on 26/9/15.
 */
public class InterestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mActivity;
    private List<Interest> interestList;
    private LayoutInflater mLayoutInflater;

    public InterestAdapter(Activity context, List<Interest> FollowingList) {
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
            if (interest != null) {
                viewHolder.textViewInterestName.setText(interest.getName());

                if (interest.getImageName() != null && !interest.getImageName().isEmpty()) {
                    Utility.setImageFromUrl(Api.BASE_URL_CLOUDINARY_BUZZZZ + interest.getImageName(), viewHolder.imageViewProfilePic, R.mipmap.ic_launcher);

                }

                if (Boolean.parseBoolean(interest.getIsSubscribed())) {
                    viewHolder.imageViewSubscribe.setBackgroundResource(R.drawable.check);

                } else {
                    viewHolder.imageViewSubscribe.setBackgroundResource(R.drawable.subscribe);

                }

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

                        if (!Boolean.parseBoolean(interest.getIsSubscribed())) {
                            subscribeInterest(interest);

                        }

                    }
                });
            }

        }
    }

    private void subscribeInterest(Interest interest) {
        requestSubscribeInerest(interest);
    }


    private void requestSubscribeInerest(final Interest interest) {
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiDetails.REQUEST_KEY_INTEREST_ID, interest.getId());


        Request request = new Request(ApiDetails.ACTION_NAME.INTEREST_SUBSCRIBE);
        request.setUrl(Api.BASE_URL_API + ApiDetails.ACTION_NAME.INTEREST_SUBSCRIBE.getActionName());
        request.setShowDialog(true);
        request.setDialogMessage(mActivity.getString(R.string.progress_dialog_msg));
        request.setParamMap(params);
        request.setRequestType(Request.HttpRequestType.POST);
        LoaderCallback loaderCallback = new LoaderCallback(mActivity, new MessageParser());
        boolean hasNetwork = loaderCallback.requestToServer(request);
        loaderCallback.setServerResponse(new APICaller() {

            @Override
            public void onComplete(Model model) {
                if (model.getStatus() == ApiDetails.STATUS_SUCCESS) {
                    interest.setIsSubscribed(String.valueOf(true));
                    SharedPreference.setBoolean(mActivity, AppConstants.PREF_KEY_HAS_INTERESTS, true);

                    notifyDataSetChanged();

                } else {
                    Utility.showToastMessage(mActivity, model.getMessage());
                }
            }
        });
        if (!hasNetwork) {
            Utility.showToastMessage(mActivity, mActivity.getString(R.string.no_network));
        }
    }

    private void showBuzzList(Interest interest) {

        Intent intent = new Intent(mActivity, BuzzListActivity.class);
        intent.putExtra(AppConstants.EXTRA_FROM, InterestActivity.TAG);
        intent.putExtra(AppConstants.EXTRA_INTEREST_NAME, interest.getName());
        intent.putExtra(AppConstants.EXTRA_INTEREST_ID, interest.getId());
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
