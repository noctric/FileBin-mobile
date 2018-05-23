package de.michael.filebinmobile.model

abstract class Upload(val id: String, val date: Long)

data class SingleUpload(val uploadTitle: String, val uploadSize: String,
                        var thumbnail: String = "", val hash: String,
                        val mimeType: String, val uploadId: String, val uploadTimeStamp: Long,
                        val uploadUrl: String = "") : Upload(uploadId, uploadTimeStamp)

data class MultiPasteUpload(val urlId: String, val uploadTimeStamp: Long,
                            val ids: List<String>) : Upload(urlId, uploadTimeStamp)