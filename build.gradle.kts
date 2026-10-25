// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.secrets) apply false
  alias(libs.plugins.google.services) apply false
}

val syncQuranApk = tasks.register("syncQuranApk") {
  doLast {
    val root = java.io.File("/app/applet")
    val oldBuildOutputs = java.io.File(root, ".build-outputs")
    if (oldBuildOutputs.exists()) {
      oldBuildOutputs.deleteRecursively()
    }
    java.io.File(root, "quran.apk").delete()
    java.io.File(root, "quran3.apk").delete()
    java.io.File(root, "quran4.apk").delete()
    java.io.File(root, "quran5.apk").delete()
    java.io.File(root, "quran6.apk").delete()
    java.io.File(root, "Quran 6.apk").delete()
    java.io.File(root, "apk/Quran 6.apk").delete()
    java.io.File(root, "quran.apk.idsig").delete()
    java.io.File(root, "quran6.apk.idsig").delete()

    val apkFile = java.io.File(root, "app/build/outputs/apk/debug/app-debug.apk")
    val targetApk = java.io.File(root, "Quran 7.apk")
    val stagedApk = java.io.File(root, "app/build/Quran_7_temp.apk")
    if (apkFile.exists()) {
      apkFile.copyTo(targetApk, overwrite = true)
      apkFile.copyTo(stagedApk, overwrite = true)
      println("Final /Quran 7.apk size: ${targetApk.length()} bytes (${String.format("%.2f", targetApk.length() / (1024.0 * 1024.0))} MB)")
    }
  }
}

subprojects {
  tasks.matching { it.name == "packageDebug" }.configureEach {
    finalizedBy(syncQuranApk)
  }
}

