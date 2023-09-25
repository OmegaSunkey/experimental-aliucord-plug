package com.aliucord.plugins.userpfp

import android.content.Context
import com.aliucord.Logger
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.Plugin
import com.aliucord.plugins.userpfp.model.APFP
import com.aliucord.plugins.userpfp.model.AbstractDatabase
import com.facebook.drawee.backends.pipeline.*
import kotlin.Throws

@AliucordPlugin
class UserPFP : Plugin() {
    init {
        APFP = com.aliucord.plugins.userpfp.model.APFP
    }
    @Throws(NoSuchMethodException::class)
    override fun start(ctx: Context) {
        APFP.init(ctx, settings, patcher)
        Fresco.initialize(ctx)
    }

    override fun stop(ctx: Context) {
        patcher.unpatchAll()
    }

    companion object {
        lateinit var APFP: APFP

        val log: Logger = Logger("UserPFP")
        const val REFRESH_CACHE_TIME = (6 * 60).toLong()
    }

    init {
        settingsTab = SettingsTab(PluginSettings::class.java).withArgs(settings)
    }
}
