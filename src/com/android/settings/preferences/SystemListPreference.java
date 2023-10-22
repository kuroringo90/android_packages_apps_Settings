/*
 * Copyright (C) 2023 The risingOS Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.android.settings.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.provider.Settings;
import android.util.AttributeSet;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.internal.util.rising.systemUtils;
import com.android.settings.R;
import java.util.Arrays;

public class SystemListPreference extends ListPreference {

    private static final String SYSTEMUI_RESTART = "systemui";
    private static final String SETTINGS_RESTART = "settings";
    private static final String SYSTEM = "system";
    private static final String SECURE = "secure";
    private static final String GLOBAL = "global";
    private static final String NONE = "none";

    public SystemListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SystemPreference);
        String restartLevel = typedArray.getString(R.styleable.SystemPreference_restart_level);
        if (restartLevel == null || restartLevel.isEmpty()) {
            restartLevel = "none";
        }
        String settingsType = typedArray.getString(R.styleable.SystemPreference_settings_type);
        if (settingsType == null || settingsType.isEmpty()) {
            settingsType = "system";
        }
        final String settingsKey = settingsType;
        final String restartKey = restartLevel;
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();
        String currentValue;
        switch (settingsKey) {
            case SYSTEM:
            default:
                currentValue = String.valueOf(Settings.System.getInt(context.getContentResolver(), getKey(), 0));
                break;
            case SECURE:
                currentValue = String.valueOf(Settings.Secure.getInt(context.getContentResolver(), getKey(), 0));
                break;
            case GLOBAL:
                currentValue = String.valueOf(Settings.Global.getInt(context.getContentResolver(), getKey(), 0));
                break;
        }
        int index = Arrays.asList(entryValues).indexOf(currentValue);
        String currentEntry = (index != -1) ? entries[index].toString() : "";
        setValue(currentValue);
        setSummary(currentEntry);
        setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int value = Integer.parseInt((String) newValue);

                int selectedIndex = Arrays.asList(entryValues).indexOf(String.valueOf(value));
                String selectedEntry = (selectedIndex != -1) ? entries[selectedIndex].toString() : "";
                setSummary(selectedEntry);
                switch (settingsKey) {
                    case SYSTEM:
                    default:
                        Settings.System.putInt(context.getContentResolver(), getKey(), value);
                        break;
                    case SECURE:
                        Settings.Secure.putInt(context.getContentResolver(), getKey(), value);
                        break;
                    case GLOBAL:
                        Settings.Global.putInt(context.getContentResolver(), getKey(), value);
                        break;
                }
                switch (restartKey) {
                    case SYSTEM:
                        systemUtils.showSystemRestartDialog(context);
                        break;
                    case SYSTEMUI_RESTART:
                        systemUtils.showSystemUIRestartDialog(context);
                        break;
                    case SETTINGS_RESTART:
                        systemUtils.showSettingsRestartDialog(context);
                        break;
                    case NONE:
                    default:
                        break;
                }

                return true;
            }
        });
    }
}
