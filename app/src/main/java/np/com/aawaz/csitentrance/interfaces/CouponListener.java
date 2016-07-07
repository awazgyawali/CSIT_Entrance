package np.com.aawaz.csitentrance.interfaces;

public interface CouponListener {
    void onCodeEntered(String set_id,String coupon_code);
    void onRequestEntered();
}
