package perklun.divebox.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import perklun.divebox.utils.Constants;

/**
 * Created by perklun on 4/23/2016.
 */
public class Dive implements Parcelable {
    User user;
    double lat;
    double lng;
    int id;

    String title;
    String date;
    String comments;
    //Entry Info
    String timeIn;
    String airIn;
    //Exit Info
    String timeOut;
    String airOut;
    String btmTime;



    public Dive(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        title = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        id = in.readInt();
        date = in.readString();
        comments = in.readString();
        timeIn = in.readString();
        airIn = in.readString();
        timeOut = in.readString();
        airOut = in.readString();
        btmTime = in.readString();
    }

    public static final Creator<Dive> CREATOR = new Creator<Dive>() {
        @Override
        public Dive createFromParcel(Parcel in) {
            return new Dive(in);
        }

        @Override
        public Dive[] newArray(int size) {
            return new Dive[size];
        }
    };

    public Dive(User newUser, String newTitle, LatLng position) {
        user = newUser;
        title = newTitle;
        lat = position.latitude;
        lng = position.longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
        dest.writeString(title);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeInt(id);
        dest.writeString(date);
        dest.writeString(comments);
        dest.writeString(timeIn);
        dest.writeString(airIn);
        dest.writeString(timeOut);
        dest.writeString(airOut);
        dest.writeString(btmTime);
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public boolean hasLatLng() {
        if (lat == Constants.INVALID_LAT || lng == Constants.INVALID_LONG) {
            return false;
        }
        return true;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public String getComments() {
        return comments;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getAirIn() {
        return airIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public String getAirOut() {
        return airOut;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public void setAirIn(String airIn) {
        this.airIn = airIn;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public void setAirOut(String airOut) {
        this.airOut = airOut;
    }

    public String getBtmTime() {
        return btmTime;
    }

    public void setBtmTime(String btmTime) {
        this.btmTime = btmTime;
    }
}
