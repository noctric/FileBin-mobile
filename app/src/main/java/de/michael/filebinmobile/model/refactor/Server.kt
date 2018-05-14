package de.michael.filebinmobile.model.refactor

import de.michael.filebinmobile.model.UserProfile

data class Server(private val address: String, private val name: String,
                  private val maxFilesPerRequest: Int, private val uploadMaxSize: Int,
                  private val requestMaxSize: Int, private val maxInputVars: Int,
                  private var userProfile: UserProfile)