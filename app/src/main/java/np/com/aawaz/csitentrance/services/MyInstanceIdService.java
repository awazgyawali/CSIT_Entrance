package np.com.aawaz.csitentrance.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //for older support todo to be removed in next year
            FirebaseDatabase.getInstance().getReference()
                    .child("instance_ids")
                    .child(user.getUid())
                    .setValue(FirebaseInstanceId.getInstance().getToken());
            //Adding data to firebase for more info
            FirebaseDatabase.getInstance().getReference()
                    .child("user_data")
                    .child(user.getUid())
                    .child("instance_id")
                    .setValue(FirebaseInstanceId.getInstance().getToken());
        }
    }
}
