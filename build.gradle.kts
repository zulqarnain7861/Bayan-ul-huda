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
      println("Removing old .build-outputs directory: ${oldBuildOutputs.deleteRecursively()}")
    }
    val apkFile = java.io.File(root, "app/build/outputs/apk/debug/app-debug.apk")
    val targetApk = java.io.File(root, "quran.apk")
    val sdkRoot = System.getenv("ANDROID_SDK_ROOT") ?: System.getenv("ANDROID_HOME") ?: "/opt/android/sdk"
    val buildToolsDir = java.io.File(sdkRoot, "build-tools")
    val latestBuildTools = buildToolsDir.listFiles()?.sortedByDescending { it.name }?.firstOrNull()

    if (apkFile.exists() && latestBuildTools != null) {
      val zipalign = java.io.File(latestBuildTools, "zipalign").absolutePath
      val apksigner = java.io.File(latestBuildTools, "apksigner").absolutePath
      val keystore = java.io.File(root, "debug.keystore")

      val alignedApk = java.io.File(root, "app/build/outputs/apk/debug/app-debug-aligned.apk")
      alignedApk.delete()
      val alignProc = ProcessBuilder(zipalign, "-p", "-f", "4", apkFile.absolutePath, alignedApk.absolutePath)
        .redirectErrorStream(true).start()
      alignProc.inputStream.bufferedReader().readText()
      alignProc.waitFor()

      val signProc = ProcessBuilder(
        apksigner, "sign",
        "--ks", keystore.absolutePath,
        "--ks-pass", "pass:android",
        "--key-pass", "pass:android",
        "--ks-key-alias", "androiddebugkey",
        "--min-sdk-version", "18",
        "--v1-signing-enabled", "true",
        "--v2-signing-enabled", "true",
        "--v3-signing-enabled", "true",
        "--out", targetApk.absolutePath,
        alignedApk.absolutePath
      ).redirectErrorStream(true).start()
      signProc.inputStream.bufferedReader().readText()
      signProc.waitFor()
      alignedApk.delete()

      // Also overwrite app-debug.apk with the V1+V2+V3 signed aligned APK
      targetApk.copyTo(apkFile, overwrite = true)

      val verifyProc = ProcessBuilder(apksigner, "verify", "--verbose", targetApk.absolutePath)
        .redirectErrorStream(true).start()
      println("apksigner verify output:\n" + verifyProc.inputStream.bufferedReader().readText())
      verifyProc.waitFor()

      println("Final /quran.apk size: ${targetApk.length()} bytes (${String.format("%.2f", targetApk.length() / (1024.0 * 1024.0))} MB)")
    }

    println("--- Workspace files > 500 KB (excluding .gradle, build, .git) ---")
    root.walkTopDown()
      .onEnter { dir -> dir.name !in setOf(".gradle", "build", ".git", ".kotlin") }
      .filter { it.isFile && it.length() > 500 * 1024 }
      .forEach { f ->
        println("  ${f.relativeTo(root).path}: ${f.length() / 1024} KB")
      }
  }
}

subprojects {
  tasks.matching { it.name == "packageDebug" }.configureEach {
    finalizedBy(syncQuranApk)
  }
}

