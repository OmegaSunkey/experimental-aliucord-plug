package com.aliucord.plugins.userpfp.model

import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.widget.ImageView
import com.aliucord.api.PatcherAPI
import com.aliucord.api.SettingsAPI
import com.aliucord.patcher.Hook
import com.discord.utilities.icon.IconUtils
import com.facebook.drawee.backends.pipeline.*
import com.facebook.drawee.view.*
import com.facebook.drawee.controller.* //abstract builder
import com.facebook.drawee.interfaces.DraweeController
import java.util.regex.Pattern
import b.f.g.e.s
import b.f.g.a.a.b //Fresco.java
import b.f.g.a.a.d //PipelineDraweeControllerBuilder.java
import com.discord.utilities.images.MGImages
import com.aliucord.plugins.userpfp.UserPFP


object APFP : AbstractDatabase() {
    override val regex: String = ".\\]\\).*?(https:\\/\\/[\\w.\\/-]*)" //.*(https:\\/\\/.*?\\.gif).*(https:\\/\\/.*?\\.png|jpg)
    override val url: String = "https://raw.githubusercontent.com/Yeetov/USRPFP-Reborn/main/db/dist.css" //https://raw.githubusercontent.com/OmegaSunkey/UserPFP-Discord/main/UserPFP.txt

    override var data: String = ""

    override val mapCache: MutableMap<Long, PFP> = HashMap()
    override val name: String = "APFP"

    override fun runPatches(patcher: PatcherAPI, settings: SettingsAPI) {
        patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
                "getForUser",
                java.lang.Long::class.java,
                String::class.java,
                Integer::class.java,
                Boolean::class.javaPrimitiveType,
                Integer::class.java
            ), Hook {
                if (it.result.toString().contains(".gif") && settings.getBool(
                        "nitroBanner",
                        true
                    )) return@Hook
                val id = it.args[0] as Long
                //it.args[3] = false
                if (mapCache.containsKey(id))
                    it.result = mapCache[id]?.let { it1 ->  it1.animated
                } else {
                	UserPFP.log.debug(regex + " this may have the problem");
                    val matcher = Pattern.compile(
                        id.toString() + regex
                    ).matcher(data)
                    if (matcher.find()) {
                    	UserPFP.log.debug(matcher.group(1).toString() + " group 1");
                    	UserPFP.log.debug(regex + " the regex");
                        mapCache[id] = PFP(matcher.group(1)).also {
                                it1 ->  it.result = it1.animated
                        }
                    }
                }

            }
    )
	patcher.patch(
            IconUtils::class.java.getDeclaredMethod(
            "setIcon", 
            ImageView::class.java, 
            String::class.java, 
            Int::class.javaPrimitiveType, 
            Int::class.javaPrimitiveType, 
            Boolean::class.javaPrimitiveType, 
            Function1::class.java, 
            MGImages.ChangeDetector::class.java
	), Hook {
                if ((it.args[1] as String).contains("https://cdn.discordapp.com/role-icons")) return@Hook

                val simpleDraweeView = it.args[0] as SimpleDraweeView
                //val a: d = b.a(); 
                //a.m = true;
                //a.n = simpleDraweeView.getController();
                //val h: AbstractDraweeController = a.a(); 
                //val h = a.newDraweeControllerBuilder().setAutoPlayGifs(false).build();
                //simpleDraweeView.setController(a.a());
                simpleDraweeView.apply {
                    hierarchy.n(s.l)
                    clipToOutline = true
                    background =
                        ShapeDrawable(OvalShape()).apply { paint.color = Color.TRANSPARENT }
                }
                UserPFP.log.debug(simpleDraweeView.getController().toString() + " drawee controller");

            })
    }

    data class PFP(val animated: String)
}
