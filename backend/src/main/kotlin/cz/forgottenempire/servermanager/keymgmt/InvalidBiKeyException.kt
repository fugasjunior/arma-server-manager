package cz.forgottenempire.servermanager.keymgmt

import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException

class InvalidBiKeyException(message: String) : CustomUserErrorException(message)
