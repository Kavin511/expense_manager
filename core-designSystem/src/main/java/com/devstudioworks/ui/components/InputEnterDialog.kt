package com.devstudioworks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.devstudio.core.designsystem.R
import com.devstudioworks.ui.theme.LARGE_SPACING
import com.devstudioworks.ui.theme.MEDIUM_SPACING
import com.devstudioworks.ui.theme.SMALL_SPACING
import com.devstudioworks.ui.theme.appColors

/**
 * @Author: Kavin
 * @Date: 29/09/23
 */

data class InputDialog(
    val heading: String = "",
    val hint: String = "",
    val positiveButtonText: String = "",
    val negativeButtonText: String = "",
    val inputLeadIcon: ImageVector? = null,
) {
    object Builder {
        private var heading: String = ""
        private var hint: String = ""
        private var positiveButtonText: String = ""
        private var negativeButtonText: String = ""
        private var inputLeadIcon: ImageVector? = null
        fun setHeading(heading: String): Builder {
            this.heading = heading
            return this
        }

        fun setInputLeadIcon(imageVector: ImageVector): Builder {
            this.inputLeadIcon = imageVector
            return this
        }

        fun setHint(hint: String): Builder {
            this.hint = hint
            return this
        }

        fun setPositiveButtonText(positiveButtonText: String): Builder {
            this.positiveButtonText = positiveButtonText
            return this
        }

        fun setNegativeButtonText(negativeButtonText: String): Builder {
            this.negativeButtonText = negativeButtonText
            return this
        }

        fun build(): InputDialog {
            return InputDialog(
                this.heading,
                this.hint,
                this.positiveButtonText,
                this.negativeButtonText,
                this.inputLeadIcon,
            )
        }
    }
}

@Preview
@Composable
fun InputEnterDialog(
    inputDialog: InputDialog = InputDialog(),
    negativeCallback: (() -> Unit)? = null,
    positiveCallback: ((String) -> Unit)? = null,
) {
    Dialog(onDismissRequest = {
        negativeCallback?.invoke()
    }) {
        var inputValue by remember {
            mutableStateOf("")
        }
        Card(shape = RoundedCornerShape(dimensionResource(id = R.dimen.default_radius))) {
            Column(
                modifier = Modifier
                    .background(appColors.material.surfaceVariant)
                    .padding(LARGE_SPACING),
            ) {
                Text(
                    text = inputDialog.heading,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(
                        SMALL_SPACING,
                    ),
                )
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = {
                        inputValue = it
                    },
                    modifier = Modifier.padding(SMALL_SPACING),
                    label = {
                        Text(text = inputDialog.hint)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.default_radius)),
                    leadingIcon = {
                        inputDialog.inputLeadIcon?.let {
                            Icon(imageVector = it, contentDescription = "")
                        }
                    },
                )
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = { negativeCallback?.invoke() },
                        modifier = Modifier.padding(SMALL_SPACING),
                    ) {
                        Text(
                            text = inputDialog.negativeButtonText,
                            modifier = Modifier.padding(MEDIUM_SPACING),
                        )
                    }
                    Button(
                        onClick = {
                            if (inputValue.isNotEmpty()) {
                                positiveCallback?.invoke(inputValue)
                            } else {
                                negativeCallback?.invoke()
                            }
                        },
                        shape = RoundedCornerShape(
                            dimensionResource(id = R.dimen.default_radius),
                        ),
                        modifier = Modifier.padding(SMALL_SPACING),
                    ) {
                        Text(
                            text = inputDialog.positiveButtonText,
                            modifier = Modifier.padding(MEDIUM_SPACING),
                        )
                    }
                }
            }
        }
    }
}
