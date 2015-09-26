package in.buzzzz.model;

import java.util.List;

/**
 * Created by Navkrishna on September 26, 2015
 */
public class BuzzPreview extends Model {
    private String buzzId;
    private String name;
    private String imageName;
    private boolean isRSVP;
    private List<String> interests;
    private Location location;
    private Schedule schedule;
    private Stats stats;

    public String getBuzzId() {
        return buzzId;
    }

    public void setBuzzId(String buzzId) {
        this.buzzId = buzzId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public boolean isRSVP() {
        return isRSVP;
    }

    public void setIsRSVP(boolean isRSVP) {
        this.isRSVP = isRSVP;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public static final class Location {
        String city;
        String latitude;
        String longitude;
        String address;
        String pincode;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }
    }

    public static final class Schedule {
        String startDate;
        String endDate;
        String startTime;
        String endTime;
        String period;

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }
    }

    public static final class Stats {
        String goingCount;
        String notComingCount;
        String mayBeCount;

        public String getGoingCount() {
            return goingCount;
        }

        public void setGoingCount(String goingCount) {
            this.goingCount = goingCount;
        }

        public String getNotComingCount() {
            return notComingCount;
        }

        public void setNotComingCount(String notComingCount) {
            this.notComingCount = notComingCount;
        }

        public String getMayBeCount() {
            return mayBeCount;
        }

        public void setMayBeCount(String mayBeCount) {
            this.mayBeCount = mayBeCount;
        }
    }
}