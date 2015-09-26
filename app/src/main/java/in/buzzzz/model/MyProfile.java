package in.buzzzz.model;

import java.util.List;

import in.buzzzz.utility.ApiDetails;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class MyProfile extends Model {
    private String id;
    private String name;
    private ApiDetails.GENDER gender;
    private String email;
    private String mobile;
    private String country;
    private Medium medium;
    private Stats stats;
    private List<Interest> interests;
    private List<BuzzPreview> buzzs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApiDetails.GENDER getGender() {
        return gender;
    }

    public void setGender(ApiDetails.GENDER gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Medium getMedium() {
        return medium;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public List<Interest> getInterests() {
        return interests;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
    }

    public List<BuzzPreview> getBuzzs() {
        return buzzs;
    }

    public void setBuzzs(List<BuzzPreview> buzzs) {
        this.buzzs = buzzs;
    }

    public static final class Stats {
        private String interestCount;
        private String buzzCount;

        public String getInterestCount() {
            return interestCount;
        }

        public void setInterestCount(String interestCount) {
            this.interestCount = interestCount;
        }

        public String getBuzzCount() {
            return buzzCount;
        }

        public void setBuzzCount(String buzzCount) {
            this.buzzCount = buzzCount;
        }
    }

    public static final class Medium {
        private String mediumId;
        private String mediumType;

        public String getMediumId() {
            return mediumId;
        }

        public void setMediumId(String mediumId) {
            this.mediumId = mediumId;
        }

        public String getMediumType() {
            return mediumType;
        }

        public void setMediumType(String mediumType) {
            this.mediumType = mediumType;
        }
    }
}