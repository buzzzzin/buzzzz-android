package in.buzzzz.model;

import java.util.List;

/**
 * Created by Rajendra Singh on 26/9/15.
 */
public class BuzzList extends Model {

    private List<Interest> mInterestList;

    public List<Interest> getmInterestList() {
        return mInterestList;
    }

    public void setmInterestList(List<Interest> mInterestList) {
        this.mInterestList = mInterestList;
    }
}
