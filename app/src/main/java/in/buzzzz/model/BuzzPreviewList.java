package in.buzzzz.model;

import java.util.List;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class BuzzPreviewList extends Model {
    List<BuzzPreview> buzzPreviews;

    public List<BuzzPreview> getBuzzPreviews() {
        return buzzPreviews;
    }

    public void setBuzzPreviews(List<BuzzPreview> buzzPreviews) {
        this.buzzPreviews = buzzPreviews;
    }
}
