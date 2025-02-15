package com.example.membershipmanagement.utils

import org.json.JSONObject


    fun extractErrorMessage(errorBody: String?): String {
        return try {
            val jsonObject = JSONObject(errorBody ?: "{}")
            val errorsObject = jsonObject.optJSONObject("errors")
            val errorMessages = mutableListOf<String>()

            errorsObject?.keys()?.forEach { key ->
                errorsObject.getJSONArray(key).let { array ->
                    for (i in 0 until array.length()) {
                        errorMessages.add(array.getString(i))
                    }
                }
            }

            errorMessages.joinToString("\n") // Gộp tất cả lỗi lại thành 1 chuỗi
        } catch (e: Exception) {
            "Lỗi không xác định"
        }
    }
