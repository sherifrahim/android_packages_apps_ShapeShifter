/*
 *  Copyright (C) 2015 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.nezuko.extras.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemProperties;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.preference.*;

import com.nezuko.support.preferences.SystemSettingSwitchPreference;
import com.nezuko.support.preferences.SystemSettingSeekBarPreference;
import com.nezuko.support.preferences.SwitchPreference;

import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class LockScreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_LOCKSCREEN_BLUR = "lockscreen_blur";
    private ContentResolver mResolver;

    private static final String KEY_TORCH_LONG_PRESS_POWER_TIMEOUT =
            "torch_long_press_power_timeout";

    private ListPreference mTorchLongPressPowerTimeout;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.nezuko_extras_lockscreen);
        PreferenceScreen prefScreen = getPreferenceScreen();
        PreferenceCategory overallPreferences = (PreferenceCategory) findPreference("fod_category");
        mResolver = getActivity().getContentResolver();

        boolean enableScreenOffFOD = getContext().getResources().
                getBoolean(R.bool.config_supportScreenOffFod);
        Preference ScreenOffFODPref = (Preference) findPreference("fod_gesture");

        if (!enableScreenOffFOD){

            overallPreferences.removePreference(ScreenOffFODPref);
        }
        
	Preference AnimaTogglePref = (Preference) findPreference("fod_recognizing_animation");
        Preference AnimaListPref = (Preference) findPreference("fod_recognizing_animation_list");            

        if (!com.android.internal.util.nezuko.Utils.isPackageInstalled(mContext,"com.nezuko.fod.animations")) {
            overallPreferences.removePreference(AnimaTogglePref);
            overallPreferences.removePreference(AnimaListPref);                
        
        if (!getResources().getBoolean(com.android.internal.R.bool.config_supportsInDisplayFingerprint)) {
            prefScreen.removePreference(findPreference("fod_category"));
        }

        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        Resources resources = getResources();

        mTorchLongPressPowerTimeout = findPreference(KEY_TORCH_LONG_PRESS_POWER_TIMEOUT);
        mTorchLongPressPowerTimeout.setOnPreferenceChangeListener(this);
        int TorchTimeout = Settings.System.getInt(getContentResolver(),
                Settings.System.TORCH_LONG_PRESS_POWER_TIMEOUT, 0);
        mTorchLongPressPowerTimeout.setValue(Integer.toString(TorchTimeout));
        mTorchLongPressPowerTimeout.setSummary(mTorchLongPressPowerTimeout.getEntry());        
        mLockscreenBlur = (SystemSettingSeekBarPreference) findPreference(KEY_LOCKSCREEN_BLUR);
        if (!com.ssos.shapeshifter.utils.Utils.isBlurSupported()) {
            mLockscreenBlur.setVisible(false);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        final String key = preference.getKey();
	    if (preference == mTorchLongPressPowerTimeout) {
            String TorchTimeout = (String) newValue;
            int TorchTimeoutValue = Integer.parseInt(TorchTimeout);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.TORCH_LONG_PRESS_POWER_TIMEOUT, TorchTimeoutValue);
            int TorchTimeoutIndex = mTorchLongPressPowerTimeout
                    .findIndexOfValue(TorchTimeout);
            mTorchLongPressPowerTimeout
                    .setSummary(mTorchLongPressPowerTimeout.getEntries()[TorchTimeoutIndex]);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.NEZUKO;
    }

}
