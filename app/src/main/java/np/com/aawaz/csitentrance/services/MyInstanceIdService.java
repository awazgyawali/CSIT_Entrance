package np.com.aawaz.csitentrance.services;

import com.google.firebase.iid.FirebaseInstanceIdService;

import np.com.aawaz.csitentrance.objects.SPHandler;

public class MyInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        SPHandler.getInstance().setScoreChanged(true);
    }
}
