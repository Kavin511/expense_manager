package com.devstudio.model.models

data class BackupStatus(val status: Status, val message: String) {
    companion object {
        fun success(message: String): BackupStatus {
            return BackupStatus(Status.SUCCESS, message)
        }

        fun failure(e: String): BackupStatus {
            return BackupStatus(Status.ERROR, e)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
}
