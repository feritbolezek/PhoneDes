package com.jonwid78.phone1

import android.content.Context

/**
 * Created by Ferit on 08-Oct-17.
 */

enum class SettingType() {
    OFFLINE_MODE, COLOURBLIND_MODE
}

class Settings(context: Context) {
    private val availableSettings = arrayOf(Setting("Save to calendar", "The app will save your data to the calendar.", SettingType.OFFLINE_MODE ,"OM", context)
    )

    fun retrieveSettings() : Array<Setting> {
        return availableSettings
    }

    fun getSetting(settingType: SettingType) : Setting {
        val setting = availableSettings.filter { it.settingType == SettingType.OFFLINE_MODE }
        return setting.first()
    }

    class Setting(val title: String,val description: String, val settingType: SettingType ,private val code: String, context: Context) {

        private val prefs = context.getSharedPreferences("settingsPrefs", Context.MODE_PRIVATE)
        private val editor = prefs.edit()

        var isActive: Boolean
            get() {
                return prefs.getBoolean(code,false)
            } set(value) {
            editor.putBoolean(code,value)
            editor.apply()
        }
//
//        private fun readPreference() : Boolean {
//           return prefs.getBoolean(code,false)
//        }
//
//        fun setPreference(value: Boolean) {
//            editor.putBoolean(code,value)
//        }

    }

}