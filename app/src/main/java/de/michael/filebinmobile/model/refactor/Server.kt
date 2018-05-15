package de.michael.filebinmobile.model.refactor

data class Server(val address: String,  val name: String,
                  val maxFilesPerRequest: Int,  val uploadMaxSize: Int,
                  val requestMaxSize: Int,  val maxInputVars: Int,
                  var userProfile: UserProfile)