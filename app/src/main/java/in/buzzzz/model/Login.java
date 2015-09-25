package in.buzzzz.model;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class Login extends Model {
    String authToken, id, name, gender, email, mediumType, mediumId;
    boolean hasInterests;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMediumType() {
        return mediumType;
    }

    public void setMediumType(String mediumType) {
        this.mediumType = mediumType;
    }

    public String getMediumId() {
        return mediumId;
    }

    public void setMediumId(String mediumId) {
        this.mediumId = mediumId;
    }

    public boolean hasInterests() {
        return hasInterests;
    }

    public void setHasInterests(boolean hasInterests) {
        this.hasInterests = hasInterests;
    }
}
