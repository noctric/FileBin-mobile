package de.michael.filebinmobile.model.refactor

import de.michael.filebinmobile.model.UserProfile

data class Server(val address: String,  val name: String,
                  val maxFilesPerRequest: Int,  val uploadMaxSize: Int,
                  val requestMaxSize: Int,  val maxInputVars: Int,
                  var userProfile: UserProfile)