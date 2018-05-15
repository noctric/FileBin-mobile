package de.michael.filebinmobile.model.refactor

data class Server(val address: String, val name: String,
                  var maxFilesPerRequest: Int, var uploadMaxSize: Int,
                  var requestMaxSize: Int, var maxInputVars: Int,
                  var userProfile: UserProfile)