package np.com.aawaz.csitentrance.objects;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Exclude;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class SetRequest {
    public String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail(),
            phone_no = SPHandler.getInstance().getPhoneNo(),
            instance_id = FirebaseInstanceId.getInstance().getToken();

    public SetRequest() {

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("phone_no", phone_no);
        result.put("instance_id", instance_id);
        result.put("active", true);
        return result;
    }
}
