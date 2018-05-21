package de.michael.filebinmobile.model

data class Upload(val uploadTitle: String, val uploadSize: String,
             var thumbnail: String = "", val hash: String,
             val mimeType: String, val id: String, val uploadTimeStamp: Long)