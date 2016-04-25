package perklun.divebox.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by perklun on 4/23/2016.
 */
public class User implements Parcelable{
    public String username;

    public User(Parcel in) {
        username = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(String newUsername) {
        username = newUsername;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
    }
}
