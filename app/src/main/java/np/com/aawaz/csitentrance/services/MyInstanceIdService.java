package np.com.aawaz.csitentrance.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import np.com.aawaz.csitentrance.objects.SPHandler;

public class MyInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        SPHandler.getInstance().setScoreChanged(true);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            FirebaseDatabase.getInstance().getReference()
                    .child("instance_ids")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(FirebaseInstanceId.getInstance().getToken());
    }
}
