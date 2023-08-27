/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * UpdateDialog.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.my.kizzy.resources.R

fun Int.formatSize(): String =
    (this / 1024f / 1024f)
        .takeIf { it > 0f }
        ?.run { " ${String.format("%.2f", this)} MB" } ?: ""
@Composable
fun UpdateDialog(
    modifier: Modifier = Modifier,
    newVersionPublishDate: String,
    newVersionSize: Int,
    newVersionLog: String,
    onDismissRequest: () -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current
    val openUrl: (url: String) -> Unit = {
        uriHandler.openUri(it)
    }
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Update,
                contentDescription = "Update",
            )
        },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = stringResource(R.string.change_log))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$newVersionPublishDate ${newVersionSize.formatSize()}",
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        text = {
            SelectionContainer {
                Text(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    text = newVersionLog,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    openUrl("https://github.com/dead8309/Kizzy/releases/latest")
                }
            ) {
                Text(
                    text = stringResource(R.string.update)
                )
            }
        },
        dismissButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
        },
    )
}

@Preview
@Composable
fun UpdateDialogPreview() {
    UpdateDialog(
        newVersionLog = "1. Fix bugs\n2. Fix bugs\n3. Fix bugs\n4. Fix bugs\n5. Fix bugs\n6. Fix bugs\n7. Fix bugs\n8. Fix bugs\n9. Fix bugs\n10. Fix bugs\n11. Fix bugs\n12. Fix bugs\n13. Fix bugs\n14. Fix bugs\n15. Fix bugs\n16. Fix bugs\n17. Fix bugs\n18. Fix bugs\n19. Fix bugs\n20. Fix bugs\n21. Fix bugs\n22. Fix bugs\n23. Fix bugs\n24. Fix bugs\n25. Fix bugs\n26. Fix bugs\n27. Fix bugs\n28. Fix bugs\n29. Fix bugs\n30. Fix bugs\n31. Fix bugs\n32. Fix bugs\n33. Fix bugs\n34. Fix bugs\n35. Fix bugs\n36. Fix bugs\n37. Fix bugs\n38. Fix bugs\n39. Fix bugs\n40. Fix bugs\n41. Fix bugs\n42. Fix bugs\n43. Fix bugs\n44. Fix bugs\n45. Fix bugs\n46. Fix bugs\n47. Fix bugs\n48. Fix bugs\n49. Fix bugs\n50. Fix bugs\n51. Fix bugs\n52. Fix bugs\n53. Fix bugs\n54. Fix bugs\n55. Fix bugs\n56. Fix bugs\n57. Fix bugs\n58. Fix bugs\n59. Fix bugs\n60. Fix bugs\n61. Fix bugs\n62. Fix bugs\n63. Fix bugs\n64. Fix bugs\n65. Fix bugs\n66. Fix bugs\n67. Fix bugs\n68. Fix bugs\n69. Fix bugs\n70. Fix bugs\n71. Fix bugs\n72. Fix bugs\n73. Fix bugs\n74. Fix bugs\n75. Fix bugs\n76. Fix bugs\n77. Fix bugs\n78. Fix bugs\n79. Fix bugs\n80. Fix bugs\n81. Fix bugs\n82. Fix bugs\n83. Fix bugs\n84. Fix bugs\n85. Fix bugs\n86. Fix bugs\n87. Fix bugs\n88. Fix bugs\n89. Fix bugs\n90. Fix bugs\n91. Fix bugs\n92. Fix bugs\n93. Fix bugs\n94. Fix bugs\n95. Fix bugs\n96. Fix bugs\n97. Fix bugs\n98. Fix bugs\n99. Fix bugs\n100. Fix bugs",
        newVersionPublishDate = "2021-10-10",
        newVersionSize = 1000000,
        onDismissRequest = {},
        modifier = Modifier
            .height(500.dp)
            .width(300.dp)
    )
}