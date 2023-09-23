package com.aliucord.plugins.userpfp

import android.content.Context
import com.aliucord.api.SettingsAPI
import com.aliucord.views.TextInput
import android.text.Editable
import android.text.InputType
import android.view.View
import com.aliucord.PluginManager
import com.aliucord.Utils
import com.aliucord.fragments.SettingsPage
import com.aliucord.plugins.userpfp.model.APFP
import com.aliucord.views.Button
import com.discord.utilities.view.text.TextWatcher
import java.lang.Exception

class PluginSettings(private val settings: SettingsAPI) : SettingsPage() {
    override fun onViewBound(view: View) {
        super.onViewBound(view)
        setActionBarTitle("UserPFP")
        val textInput = TextInput(view.context)
        //textInput.hint = "Refresh UserPFP database time (minutes)"
        textInput.editText!!.setText(
            settings.getLong("cacheTime", UserPFP.REFRESH_CACHE_TIME).toString()
        )
        textInput.editText!!.inputType = InputType.TYPE_CLASS_NUMBER
        textInput.editText!!.addTextChangedListener(object : TextWatcher() {
            override fun afterTextChanged(editable: Editable) {
                try {
                    if (java.lang.Long.valueOf(editable.toString()) != 0L) settings.setLong(
                        "cacheTime",
                        java.lang.Long.valueOf(editable.toString())
                    )
                } catch (e: Exception) {
                    settings.setLong("cacheTime", UserPFP.REFRESH_CACHE_TIME)
                }
            }
        })
        val refreshCache = Button(view.context)
        refreshCache.text = "Redownload databases"
        refreshCache.setOnClickListener { button: View? ->
            Utils.threadPool.execute {
                Utils.showToast("Downloading databases...")
                context?.let { UserPFP.APFP.getCacheFile(it) }?.let { UserPFP.APFP.downloadDB(it) }
                Utils.showToast("Downloaded databases.")

                with(UserPFP.APFP) {
                    Utils.showToast("Downloading databases...")

                    Utils.showToast("Downloaded databases.")
                }
            }
        }
        addView(textInput)
        addView(refreshCache)
    }

}
