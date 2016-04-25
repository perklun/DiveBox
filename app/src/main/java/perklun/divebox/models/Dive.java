package perklun.divebox.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by perklun on 4/23/2016.
 */
public class Dive implements Parcelable{
    public User user;
    public String title;

    public Dive(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        title = in.readString();
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

    public Dive(User newUser, String newTitle) {
        user = newUser;
        title = newTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
        dest.writeString(title);
    }
}
