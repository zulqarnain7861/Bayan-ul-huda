package com.example.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

enum class OperationType {
  CREATE,
  UPDATE,
  DELETE,
  LIST,
  GET,
  WRITE
}

fun handleFirestoreError(
  error: Exception,
  operationType: OperationType,
  path: String?
) {
  val auth = try {
    FirebaseAuth.getInstance()
  } catch (e: Exception) {
    null
  }
  val currentUser = auth?.currentUser

  val providerArray = org.json.JSONArray()
  currentUser?.providerData?.forEach { userInfo ->
    providerArray.put(
      JSONObject().apply {
        put("providerId", userInfo.providerId)
        put("displayName", userInfo.displayName ?: JSONObject.NULL)
        put("email", userInfo.email ?: JSONObject.NULL)
        put("photoUrl", userInfo.photoUrl?.toString() ?: JSONObject.NULL)
      }
    )
  }

  val authInfo = JSONObject().apply {
    put("userId", currentUser?.uid ?: JSONObject.NULL)
    put("email", currentUser?.email ?: JSONObject.NULL)
    put("emailVerified", currentUser?.isEmailVerified ?: JSONObject.NULL)
    put("isAnonymous", currentUser?.isAnonymous ?: JSONObject.NULL)
    put("tenantId", currentUser?.tenantId ?: JSONObject.NULL)
    put("providerInfo", providerArray)
  }

  val errInfo = JSONObject().apply {
    put("error", error.message ?: error.toString())
    put("operationType", operationType.name.lowercase())
    put("path", path ?: JSONObject.NULL)
    put("authInfo", authInfo)
  }

  Log.e("FirestoreError", errInfo.toString())
  throw error
}
