package in.buzzzz.model;

import java.util.List;

/**
 * Created by Rajendra Singh on 26/9/15.
 */
public class BuzzList extends Model {

    private List<Interest> interestList;
    private  List<BuzzPreview> buzzPreviewList;

    public List<Interest> getInterestList() {
        return interestList;
    }

    public void setInterestList(List<Interest> interestList) {
        this.interestList = interestList;
    }

    public List<BuzzPreview> getBuzzPreviewList() {
        return buzzPreviewList;
    }

    public void setBuzzPreviewList(List<BuzzPreview> buzzPreviewList) {
        this.buzzPreviewList = buzzPreviewList;
    }
}
