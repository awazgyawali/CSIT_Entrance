package np.com.aawaz.csitentrance.Utils;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;

import java.util.Map;
import java.util.Set;

import np.com.aawaz.csitentrance.misc.MyApplication;

import static np.com.aawaz.csitentrance.Utils.ENUM.algorithm_key;
import static np.com.aawaz.csitentrance.Utils.ENUM.encryption_key;

/*
CREated by avi(Wisdomrider)
on 9/15/2018
*/
public class Preferences implements SharedPreferences,SharedPreferences.Editor {
    SharedPreferences preferences;
    Encryption encryption;
    Editor editor;
    public Preferences(String dbname, int mode) {
        preferences = MyApplication.getAppContext().getSharedPreferences(dbname, mode);
        encryption = new Encryption(encryption_key, algorithm_key); //change it later from enum
        editor=edit();
    }


    @Override
    public Map<String, ?> getAll() {
        return preferences.getAll();
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        return decrypt(preferences.getString(key, defValue));
    }

    public String encrypt(String textToEncrypt) {
        try {
            byte[] b = encryption.encrypt(textToEncrypt);
            return Encryption.bytesToHex(b);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return "";
    }

    public String decrypt(String textToDecrypt) {
        try {
            return new String(encryption.decrypt(textToDecrypt));
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return "";
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return preferences.getStringSet(key,defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return Integer.parseInt(decrypt(preferences.getString(key, Integer.toString(defValue))));
    }

    @Override
    public long getLong(String key, long defValue) {
        return Long.parseLong(decrypt(preferences.getString(key, Long.toString(defValue))));
    }

    @Override
    public float getFloat(String key, float defValue) {
        return Float.parseFloat(decrypt(preferences.getString(key, Float.toString(defValue))));
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(encrypt(key), defValue);

    }

    @Override
    public boolean contains(String key) {
        return preferences.contains(key);
    }

    @Override
    public Editor edit() {
        return preferences.edit();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }


    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public Editor putString(String key, @Nullable String value) {
        return editor.putString(key,encrypt(value));
    }

    @Override
    public Editor putStringSet(String key, @Nullable Set<String> values) {
        return editor.putStringSet(key,values);
    }

    @Override
    public Editor putInt(String key, int value) {
        return editor.putString(key,encrypt(Integer.toString(value)));
    }

    @Override
    public Editor putLong(String key, long value) {
        return editor.putString(key,encrypt(Long.toString(value)));
    }

    @Override
    public Editor putFloat(String key, float value) {
        return editor.putString(key,encrypt(Float.toString(value)));

    }

    @Override
    public Editor putBoolean(String key, boolean value) {
        return editor.putBoolean(encrypt(key),value);
    }

    @Override
    public Editor remove(String key) {
        return editor.remove(key);
    }

    @Override
    public Editor clear() {
        return editor.clear();
    }

    @Override
    public boolean commit() {
        return editor.commit();
    }

    @Override
    public void apply() {
        editor.apply();
    }
}
