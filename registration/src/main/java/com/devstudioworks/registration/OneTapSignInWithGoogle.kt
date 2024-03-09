package com.devstudioworks.registration

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch

@Composable
fun rememberOneTapSignInState(): OneTapSignInState {
    return rememberSaveable(
        saver = OneTapSignInStateSaver
    ) { OneTapSignInState() }
}

private const val TAG = "OneTapCompose"

@Composable
fun OneTapSignInWithGoogle(
    state: OneTapSignInState,
    clientId: String,
    rememberAccount: Boolean = true,
    nonce: String? = null,
    onTokenIdReceived: (String) -> Unit,
    onDialogDismissed: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }

    val googleIdOption = remember {
        GetGoogleIdOption.Builder()
            .setServerClientId(clientId)
            .setNonce(nonce)
            .setFilterByAuthorizedAccounts(rememberAccount)
            .build()
    }

    val request = remember {
        GetCredentialRequest.Builder()
            .setCredentialOptions(listOf(googleIdOption))
            .build()
    }

    LaunchedEffect(key1 = state.opened) {
        if (state.opened) {
            scope.launch {
                try {
                    val response = credentialManager.getCredential(
                        request = request,
                        context = context,
                    )
                    handleSignIn(
                        credentialResponse = response,
                        onTokenIdReceived = {
                            onTokenIdReceived(it)
                            state.close()
                        },
                        onDialogDismissed = {
                            Log.e(TAG, it)
                            onDialogDismissed(it)
                            state.close()
                        }
                    )
                } catch (e: GetCredentialException) {
                    if (e.message != null) {
                        if (e.message!!.contains("No credentials available")) {
                            handleCredentialsNotAvailable(
                                context = context,
                                state = state,
                                credentialManager = credentialManager,
                                clientId = clientId,
                                nonce = nonce,
                                onTokenIdReceived = onTokenIdReceived,
                                onDialogDismissed = onDialogDismissed
                            )
                        }
                    } else {
                        try {
                            val errorMessage = if (e.message != null) {
                                if (e.message!!.contains("activity is cancelled by the user.")) {
                                    "Dialog Closed."
                                } else if (e.message!!.contains("Caller has been temporarily blocked")) {
                                    "Sign in has been Temporarily Blocked due to too many Closed Prompts."
                                } else {
                                    e.message.toString()
                                }
                            } else "Unknown Error."
                            Log.e(TAG, errorMessage)
                            onDialogDismissed(errorMessage)
                            state.close()
                        } catch (e: Exception) {
                            Log.e(TAG, "${e.message}")
                            onDialogDismissed("${e.message}")
                            state.close()
                        }
                    }
                } catch (e: Exception) {
                    if (e.message != null) {
                        if (e.message!!.contains("No credentials available")) {
                            handleCredentialsNotAvailable(
                                context = context,
                                state = state,
                                credentialManager = credentialManager,
                                clientId = clientId,
                                nonce = nonce,
                                onTokenIdReceived = onTokenIdReceived,
                                onDialogDismissed = onDialogDismissed
                            )
                        }
                    } else {
                        Log.e(TAG, "${e.message}")
                        onDialogDismissed("${e.message}")
                        state.close()
                    }
                }
            }
        }
    }
}

private fun handleSignIn(
    credentialResponse: GetCredentialResponse,
    onTokenIdReceived: (String) -> Unit,
    onDialogDismissed: (String) -> Unit,
) {
    when (val credential = credentialResponse.credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)
                    onTokenIdReceived(googleIdTokenCredential.idToken)
                } catch (e: GoogleIdTokenParsingException) {
                    onDialogDismissed("Invalid Google tokenId response: ${e.message}")
                }
            } else {
                onDialogDismissed("Unexpected Type of Credential.")
            }
        }

        else -> {
            onDialogDismissed("Unexpected Type of Credential.")
        }
    }
}

private suspend fun handleCredentialsNotAvailable(
    context: Context,
    state: OneTapSignInState,
    credentialManager: CredentialManager,
    clientId: String,
    nonce: String?,
    onTokenIdReceived: (String) -> Unit,
    onDialogDismissed: (String) -> Unit
) {
    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(clientId)
        .setNonce(nonce)
        .setFilterByAuthorizedAccounts(false)
        .build()

    val request = GetCredentialRequest.Builder()
        .setCredentialOptions(listOf(googleIdOption))
        .build()

    try {
        val response = credentialManager.getCredential(
            request = request,
            context = context,
        )
        handleSignIn(
            credentialResponse = response,
            onTokenIdReceived = {
                onTokenIdReceived(it)
                state.close()
            },
            onDialogDismissed = {
                Log.e(TAG, it)
                onDialogDismissed(it)
                state.close()
            }
        )
    } catch (e: GetCredentialException) {
        try {
            if (e.message!!.contains("No credentials available")) {
                openGoogleAccountSettings(context = context)
            }
            val errorMessage = if (e.message != null) {
                if (e.message!!.contains("activity is cancelled by the user.")) {
                    "Dialog Closed."
                } else if (e.message!!.contains("Caller has been temporarily blocked")) {
                    "Sign in has been Temporarily Blocked due to too many Closed Prompts."
                } else {
                    e.message.toString()
                }
            } else "Unknown Error."
            Log.e(TAG, errorMessage)
            onDialogDismissed(errorMessage)
            state.close()
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
            onDialogDismissed("${e.message}")
            state.close()
        }
    } catch (e: Exception) {
        Log.e(TAG, "${e.message}")
        onDialogDismissed("${e.message}")
        state.close()
    }
}

fun openGoogleAccountSettings(context: Context) {
    try {
        val addAccountIntent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
            putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        }
        context.startActivity(addAccountIntent)
    } catch (e: Exception) {
        Log.e(TAG, "openGoogleAccountSettings Error: $e")
    }
}

private val OneTapSignInStateSaver: Saver<OneTapSignInState, Boolean> = Saver(
    save = { state -> state.opened },
    restore = { opened -> OneTapSignInState(open = opened) },
)