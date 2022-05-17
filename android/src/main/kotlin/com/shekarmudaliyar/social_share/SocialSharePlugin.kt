package com.shekarmudaliyar.social_share

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File


/** SocialSharePlugin */
class SocialSharePlugin(private val registrar: Registrar):  MethodCallHandler {

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "social_share")
      channel.setMethodCallHandler(SocialSharePlugin(registrar))
    }
  }
  @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
      if (call.method == "shareInstagramStory") {
          //share on instagram story
              val stickerImage: String? = call.argument("stickerImage")
          val backgroundImage: String? = call.argument("backgroundImage")

          val backgroundTopColor: String? = call.argument("backgroundTopColor")
              val backgroundBottomColor: String? = call.argument("backgroundBottomColor")
              val attributionURL: String? = call.argument("attributionURL")
            val file =  File(registrar.activeContext().cacheDir,stickerImage)
          val stickerImageFile = FileProvider.getUriForFile(registrar.activeContext(), registrar.activeContext().applicationContext.packageName + ".com.shekarmudaliyar.social_share", file)

          val intent = Intent("com.instagram.share.ADD_TO_STORY")
              intent.type = "image/*"
          intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
              intent.putExtra("interactive_asset_uri", stickerImageFile)
          if(backgroundImage!=null){
              //check if background image is also provided
              val backfile =  File(registrar.activeContext().cacheDir,backgroundImage)
              val backgroundImageFile = FileProvider.getUriForFile(registrar.activeContext(), registrar.activeContext().applicationContext.packageName + ".com.shekarmudaliyar.social_share", backfile)
              intent.setDataAndType(backgroundImageFile,"image/*")
          }

          intent.putExtra("content_url", attributionURL)
              intent.putExtra("top_background_color", backgroundTopColor)
              intent.putExtra("bottom_background_color", backgroundBottomColor)
              Log.d("", registrar.activity().toString())
              // Instantiate activity and verify it will resolve implicit intent
              val activity: Activity = registrar.activity()
              activity.grantUriPermission(
                      "com.instagram.android", stickerImageFile, Intent.FLAG_GRANT_READ_URI_PERMISSION)
              if (activity.packageManager.resolveActivity(intent, 0) != null) {
                  registrar.activeContext().startActivity(intent)
                  result.success("success")
              } else {
                  result.success("error")
              }
      }else if(call.method == "shareFacebookStory"){
          //share on facebook story
          val stickerImage: String? = call.argument("stickerImage")
          val backgroundTopColor: String? = call.argument("backgroundTopColor")
          val backgroundBottomColor: String? = call.argument("backgroundBottomColor")
          val attributionURL: String? = call.argument("attributionURL")
          val appId: String? = call.argument("appId")

          val file =  File(registrar.activeContext().cacheDir,stickerImage)
          val stickerImageFile = FileProvider.getUriForFile(registrar.activeContext(), registrar.activeContext().applicationContext.packageName + ".com.shekarmudaliyar.social_share", file)
          val intent = Intent("com.facebook.stories.ADD_TO_STORY")
          intent.type = "image/*"
          intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
          intent.putExtra("com.facebook.platform.extra.APPLICATION_ID", appId)
          intent.putExtra("interactive_asset_uri", stickerImageFile)
          intent.putExtra("content_url", attributionURL)
          intent.putExtra("top_background_color", backgroundTopColor)
          intent.putExtra("bottom_background_color", backgroundBottomColor)
          Log.d("", registrar.activity().toString())
          // Instantiate activity and verify it will resolve implicit intent
          val activity: Activity = registrar.activity()
          activity.grantUriPermission(
                  "com.facebook.katana", stickerImageFile, Intent.FLAG_GRANT_READ_URI_PERMISSION)
          if (activity.packageManager.resolveActivity(intent, 0) != null) {
              registrar.activeContext().startActivity(intent)
              result.success("success")
          } else {
              result.success("error")
          }
      }else if(call.method == "shareOptions"){
          //native share options

          val content: String? = call.argument("content")
          val image: String? = call.argument("image")
          val intent = Intent()
          intent.action = Intent.ACTION_SEND

          if(image!=null){
              //check if  image is also provided
              val imagefile =  File(registrar.activeContext().cacheDir,image)
              val imageFileUri = FileProvider.getUriForFile(registrar.activeContext(), registrar.activeContext().applicationContext.packageName + ".com.shekarmudaliyar.social_share", imagefile)
              intent.type = "image/*"
              intent.putExtra(Intent.EXTRA_STREAM,imageFileUri)
          } else {
              intent.type = "text/plain";
          }

          intent.putExtra(Intent.EXTRA_TEXT, content)

          //create chooser intent to launch intent
          //source: "share" package by flutter (https://github.com/flutter/plugins/blob/master/packages/share/)
          val chooserIntent: Intent = Intent.createChooser(intent, null /* dialog title optional */)

          registrar.activeContext().startActivity(chooserIntent)
          result.success(true)

      }else if(call.method == "copyToClipboard"){
          //copies content onto the clipboard
          val content: String? = call.argument("content")
          val clipboard =registrar.context().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
          val clip = ClipData.newPlainText("", content)
          clipboard.setPrimaryClip(clip)
          result.success(true)
      }else if(call.method == "shareFacebook"){
          //shares content on WhatsApp
          val content: String? = call.argument("content")
          val image: String? = call.argument("image")

          val facebookIntent = Intent(Intent.ACTION_SEND)
          if(image!=null){
              //check if  image is also provided
              val imagefile =  File(registrar.activeContext().cacheDir,image)
              val imageFileUri = FileProvider.getUriForFile(registrar.activeContext(), registrar.activeContext().applicationContext.packageName + ".com.shekarmudaliyar.social_share", imagefile)
              facebookIntent.type = "image/*"
              facebookIntent.putExtra(Intent.EXTRA_STREAM,imageFileUri)
          } else {
              facebookIntent.type = "text/plain";
              facebookIntent.putExtra(Intent.EXTRA_TEXT, content);
              facebookIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(content));
          }
          facebookIntent.setPackage("com.facebook.katana")
          facebookIntent.setClassName("com.facebook.katana",
            "com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias");
        //   facebookIntent.putExtra(Intent.EXTRA_TEXT, content)
          try {
              registrar.activity().startActivity(facebookIntent)
              result.success("true")
          } catch (ex: ActivityNotFoundException) {
              result.success("false")
          }
      }else if(call.method == "shareWhatsapp"){
          //shares content on WhatsApp
          val content: String? = call.argument("content")
          val image: String? = call.argument("image")
          val phoneNumber: String? = call.argument("phoneNumber")

          val whatsappIntent = Intent(Intent.ACTION_SEND)
          if(image!=null){
              //check if  image is also provided
              val imagefile =  File(registrar.activeContext().cacheDir,image)
              val imageFileUri = FileProvider.getUriForFile(registrar.activeContext(), registrar.activeContext().applicationContext.packageName + ".com.shekarmudaliyar.social_share", imagefile)
              whatsappIntent.type = "image/*"
              whatsappIntent.putExtra(Intent.EXTRA_STREAM,imageFileUri)
          } else {
              whatsappIntent.type = "text/plain";
          }
          if(phoneNumber != null){
              whatsappIntent.putExtra("jid", phoneNumber + "@s.whatsapp.net");
          }
          whatsappIntent.setPackage("com.whatsapp")
          whatsappIntent.putExtra(Intent.EXTRA_TEXT, content)
          try {
              registrar.activity().startActivity(whatsappIntent)
              result.success("true")
          } catch (ex: ActivityNotFoundException) {
              result.success("false")
          }
      }else if(call.method == "shareSms"){
          //shares content on sms
          val content: String? = call.argument("content")
          val image: String? = call.argument("image")
          val phoneNumber: String? = call.argument("phoneNumber")

        if(image!=null){
          val imagefile =  File(registrar.activeContext().cacheDir,image)
          val imageFileUri = FileProvider.getUriForFile(registrar.activeContext(), registrar.activeContext().applicationContext.packageName + ".com.shekarmudaliyar.social_share", imagefile)

          val intent = Intent(Intent.ACTION_SENDTO)
          intent.addCategory(Intent.CATEGORY_DEFAULT)
          intent.type = "vnd.android-dir/mms-sms"
          intent.data = Uri.parse("sms:" + phoneNumber)
          intent.putExtra("sms_body", content)
          intent.putExtra(Intent.EXTRA_STREAM,imageFileUri)
          try {
              registrar.activity().startActivity(intent)
              result.success("true")
          } catch (ex: ActivityNotFoundException) {
              result.success("false")
          }
        }else{
          val intent = Intent(Intent.ACTION_SENDTO)
          intent.addCategory(Intent.CATEGORY_DEFAULT)
          intent.type = "vnd.android-dir/mms-sms"
          intent.data = Uri.parse("sms:" + phoneNumber)
          intent.putExtra("sms_body", content)
          try {
              registrar.activity().startActivity(intent)
              result.success("true")
          } catch (ex: ActivityNotFoundException) {
              result.success("false")
          }
        }
          
      }else if(call.method == "shareInstagram"){
          //shares content on Twitter
          val content: String? = call.argument("content")
          val image: String? = call.argument("image")

          val instagramIntent = Intent(Intent.ACTION_SEND)
          if(image!=null){
              //check if  image is also provided
              val imagefile =  File(registrar.activeContext().cacheDir,image)
              val imageFileUri = FileProvider.getUriForFile(registrar.activeContext(), registrar.activeContext().applicationContext.packageName + ".com.shekarmudaliyar.social_share", imagefile)
              instagramIntent.type = "image/*"
              instagramIntent.putExtra(Intent.EXTRA_STREAM,imageFileUri)
          } else {
              instagramIntent.type = "text/plain";
          }

          instagramIntent.setPackage("com.instagram.android")
          instagramIntent.setClassName("com.instagram.android",
            "com.instagram.share.handleractivity.ShareHandlerActivity");
            
          instagramIntent.putExtra(Intent.EXTRA_TEXT, content)
          try {
              registrar.activity().startActivity(instagramIntent)
              result.success("true")
          } catch (ex: ActivityNotFoundException) {
              result.success("false")
          }
      }else if(call.method == "shareTwitter"){
          //shares content on WhatsApp
          val content: String? = call.argument("content")
          val image: String? = call.argument("image")

          val twitterIntent = Intent(Intent.ACTION_SEND)
          if(image!=null){
              //check if  image is also provided
              val imagefile =  File(registrar.activeContext().cacheDir,image)
              val imageFileUri = FileProvider.getUriForFile(registrar.activeContext(), registrar.activeContext().applicationContext.packageName + ".com.shekarmudaliyar.social_share", imagefile)
              twitterIntent.type = "image/*"
              twitterIntent.putExtra(Intent.EXTRA_STREAM,imageFileUri)
          } else {
              twitterIntent.type = "text/plain";
          }
          twitterIntent.setPackage("com.twitter.android")
          twitterIntent.setClassName("com.twitter.android",
            "com.twitter.composer.ComposerActivity");
          twitterIntent.putExtra(Intent.EXTRA_TEXT, content)
          try {
              registrar.activity().startActivity(twitterIntent)
              result.success("true")
          } catch (ex: ActivityNotFoundException) {
              result.success("false")
          }
      }else if(call.method == "shareTwitterDM"){
          //shares content on Twitter
          val content: String? = call.argument("content")
          val image: String? = call.argument("image")

          val twitterDMIntent = Intent(Intent.ACTION_SEND)
          if(image!=null){
              //check if  image is also provided
              val imagefile =  File(registrar.activeContext().cacheDir,image)
              val imageFileUri = FileProvider.getUriForFile(registrar.activeContext(), registrar.activeContext().applicationContext.packageName + ".com.shekarmudaliyar.social_share", imagefile)
              twitterDMIntent.type = "image/*"
              twitterDMIntent.putExtra(Intent.EXTRA_STREAM,imageFileUri)
          } else {
              twitterDMIntent.type = "text/plain";
          }

          twitterDMIntent.setPackage("com.twitter.android")
          twitterDMIntent.setClassName("com.twitter.android",
            "com.twitter.app.dm.DMActivity");
          twitterDMIntent.putExtra(Intent.EXTRA_TEXT, content)
          try {
              registrar.activity().startActivity(twitterDMIntent)
              result.success("true")
          } catch (ex: ActivityNotFoundException) {
              result.success("false")
          }
      }
      else if(call.method == "shareInstagramDM"){
          //shares content on Instagram Chats
          val content: String? = call.argument("content")
          val instagramDMIntent = Intent(Intent.ACTION_SEND)
          instagramDMIntent.type = "text/plain"
          instagramDMIntent.setPackage("com.instagram.android")
        //   twitterDMIntent.setClassName("com.instagram.android",
        //     "com.instagram.direct.share.handler.DirectShareHandlerActivityInterop");
          instagramDMIntent.putExtra(Intent.EXTRA_TEXT, content)
          try {
              registrar.activity().startActivity(instagramDMIntent)
              result.success("true")
          } catch (ex: ActivityNotFoundException) {
              result.success("false")
          }
      }
      else if(call.method == "shareTelegram"){
          //shares content on Telegram
          val content: String? = call.argument("content")
          val image: String? = call.argument("image")

          val telegramIntent = Intent(Intent.ACTION_SEND)
          if(image!=null){
              //check if  image is also provided
              val imagefile =  File(registrar.activeContext().cacheDir,image)
              val imageFileUri = FileProvider.getUriForFile(registrar.activeContext(), registrar.activeContext().applicationContext.packageName + ".com.shekarmudaliyar.social_share", imagefile)
              telegramIntent.type = "image/*"
              telegramIntent.putExtra(Intent.EXTRA_STREAM,imageFileUri)
          } else {
              telegramIntent.type = "text/plain";
          }

          telegramIntent.setPackage("org.telegram.messenger")
          telegramIntent.putExtra(Intent.EXTRA_TEXT, content)
          try {
              registrar.activity().startActivity(telegramIntent)
              result.success("true")
          } catch (ex: ActivityNotFoundException) {
              result.success("false")

          }
      }else if(call.method == "shareEmail"){
          //shares content on Telegram
          val content: String? = call.argument("content")
          val image: String? = call.argument("image")

          val emailIntent = Intent(Intent.ACTION_SEND)
          if(image!=null){
              //check if  image is also provided
              val imagefile =  File(registrar.activeContext().cacheDir,image)
              val imageFileUri = FileProvider.getUriForFile(registrar.activeContext(), registrar.activeContext().applicationContext.packageName + ".com.shekarmudaliyar.social_share", imagefile)
              emailIntent.type = "image/*"
              emailIntent.putExtra(Intent.EXTRA_STREAM,imageFileUri)
          } else {
              emailIntent.type = "text/plain";
          }

          emailIntent.setPackage("com.google.android.gm")
          emailIntent.setClassName("com.google.android.gm",
            "com.google.android.gm.ComposeActivityGmailExternal");
          emailIntent.putExtra(Intent.EXTRA_TEXT, content)
          try {
              registrar.activity().startActivity(emailIntent)
              result.success("true")
          } catch (ex: ActivityNotFoundException) {
              result.success("false")

          }
      } else if (call.method == "shareWABusiness") {
          //shares content on WhatsApp for Business
          val content: String? = call.argument("content")
          val image: String? = call.argument("image")
          val phoneNumber: String? = call.argument("phoneNumber")

          val whatsappBusinessIntent = Intent(Intent.ACTION_SEND)
          if(image!=null){
              //check if  image is also provided
              val imagefile =  File(registrar.activeContext().cacheDir,image)
              val imageFileUri = FileProvider.getUriForFile(registrar.activeContext(), registrar.activeContext().applicationContext.packageName + ".com.shekarmudaliyar.social_share", imagefile)
              whatsappBusinessIntent.type = "image/*"
              whatsappBusinessIntent.putExtra(Intent.EXTRA_STREAM,imageFileUri)
          } else {
              whatsappBusinessIntent.type = "text/plain";
          }
          if(phoneNumber != null){
              whatsappBusinessIntent.putExtra("jid", phoneNumber + "@s.whatsapp.net");
          }
          whatsappBusinessIntent.setPackage("com.whatsapp.w4b")
          whatsappBusinessIntent.putExtra(Intent.EXTRA_TEXT, content)
          try {
              registrar.activity().startActivity(whatsappBusinessIntent)
              result.success("true")
          } catch (ex: ActivityNotFoundException) {
              result.success("false")
          }
      } else if (call.method == "verifyWhatsAppNumber" || call.method == "verifyWhatsAppBusinessNumber") {
          //verify Whatsapp number by setting the data with wa.me content
          val content: String? = call.argument("content")
          val phoneNumber: String? = call.argument("phoneNumber")
          val whatsappIntent = Intent(Intent.ACTION_SEND)
          whatsappIntent.type = "text/plain";
          whatsappIntent.setData(Uri.parse("https://wa.me/"+phoneNumber+"?text=%20"))
          if(call.method == "verifyWhatsAppNumber"){
              whatsappIntent.setPackage("com.whatsapp")
          whatsappIntent.setClassName("com.whatsapp",
            "com.whatsapp.TextAndDirectChatDeepLink")
          }else if(call.method == "verifyWhatsAppBusinessNumber"){
            whatsappIntent.setPackage("com.whatsapp.w4b")
          whatsappIntent.setClassName("com.whatsapp.w4b",
            "com.whatsapp.TextAndDirectChatDeepLink");
          }
          whatsappIntent.putExtra(Intent.EXTRA_TEXT, content)
          try {
              registrar.activity().startActivity(whatsappIntent)
              result.success("true")
          } catch (ex: ActivityNotFoundException) {
              result.success("false")
          }
      } else if(call.method == "checkInstalledApps"){
          //check if the apps exists
          //creating a mutable map of apps
          var apps:MutableMap<String, Boolean> = mutableMapOf()
          //assigning package manager
          val pm: PackageManager =registrar.context().packageManager
          //get a list of installed apps.
          val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
          //intent to check sms app exists
          val intent = Intent(Intent.ACTION_SENDTO).addCategory(Intent.CATEGORY_DEFAULT)
          intent.type = "vnd.android-dir/mms-sms"
          intent.data = Uri.parse("sms:" )
          val resolvedActivities: List<ResolveInfo>  = pm.queryIntentActivities(intent, 0)
          //if sms app exists
          apps["sms"] = resolvedActivities.isNotEmpty()
          //if other app exists
          apps["instagram"] = packages.any  {it.packageName.toString().contentEquals("com.instagram.android")  }
          apps["facebook"] = packages.any  { it.packageName.toString().contentEquals("com.facebook.katana")}
          apps["twitter"] = packages.any  {it.packageName.toString().contentEquals("com.twitter.android")}
          apps["whatsapp"] = packages.any  {it.packageName.toString().contentEquals("com.whatsapp")}
          apps["telegram"] = packages.any  {it.packageName.toString().contentEquals("org.telegram.messenger")  }
          apps["whatsapp_business"] = packages.any  {it.packageName.toString().contentEquals("com.whatsapp.w4b")  }

          result.success(apps)
          } else {
          result.notImplemented()
      }
  }
}
