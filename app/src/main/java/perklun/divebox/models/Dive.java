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
    String title;
    double lat;
    double lng;
    int id;

    public Dive(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        title = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        id = in.readInt();
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
}
