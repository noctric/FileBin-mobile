package de.michael.filebinmobile.model

data class Server(val address: String, val name: String,
                  var maxFilesPerRequest: Int = 0, var uploadMaxSize: Int = 0,
                  var requestMaxSize: Int = 0, var maxInputVars: Int = 0,
                  var userProfile: UserProfile? = null)

fun Server.toPostInfo(): PostInfo = PostInfo(this, this.userProfile!!)