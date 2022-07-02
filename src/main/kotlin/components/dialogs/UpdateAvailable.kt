/**

HashHash
Copyright (C) 2022  Russell Banks

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

 */

package components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import api.Ktor
import application.DialogState
import com.russellbanks.HashHash.BuildConfig
import components.dialogs.about.DialogHeader
import helper.Browser
import helper.GitHub
import helper.Icons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.pushingpixels.aurora.component.model.Command
import org.pushingpixels.aurora.component.model.LabelContentModel
import org.pushingpixels.aurora.component.model.LabelPresentationModel
import org.pushingpixels.aurora.component.projection.CommandButtonProjection
import org.pushingpixels.aurora.component.projection.LabelProjection
import org.pushingpixels.aurora.theming.AuroraSkin
import java.net.URL

@Composable
fun UpdateAvailableDialog(dialogState: DialogState, ktor: Ktor) {
    val backgroundColorScheme = AuroraSkin.colors.getBackgroundColorScheme(
        decorationAreaType = AuroraSkin.decorationAreaType
    )
    val scope = rememberCoroutineScope() { Dispatchers.Default }
    AnimatedVisibility(
        visible = dialogState.Update().isOpen(),
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 10 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 10 })
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.width(450.dp),
                shape = RoundedCornerShape(8.dp),
                color = backgroundColorScheme.accentedBackgroundFillColor,
                border = BorderStroke(1.dp, Color.Black),
                elevation = 4.dp
            ) {
                Column {
                    DialogHeader(dialogState = dialogState, dialog = DialogState.Dialogs.Update)
                    Row(Modifier.padding(30.dp)) {
                        Box(Modifier.padding(end = 30.dp)) {
                            Image(
                                painter = Icons.logo(),
                                contentDescription = "${BuildConfig.appName} logo",
                                modifier = Modifier.size(60.dp)
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LabelProjection(
                                contentModel = LabelContentModel(text = "There is a new update available!"),
                                presentationModel = LabelPresentationModel(
                                    textStyle = TextStyle(fontWeight = FontWeight.SemiBold)
                                )
                            ).project()
                            LabelProjection(
                                contentModel = LabelContentModel(
                                    text = "${BuildConfig.appVersion} ➝ ${ktor.getReleasedVersion()}"
                                )
                            ).project()
                            CommandButtonProjection(
                                contentModel = Command(
                                    text = "Go to release page",
                                    action = {
                                        scope.launch {
                                            Browser.open(
                                                URL(ktor.githubData?.htmlUrl ?: GitHub.HashHash.Repository.releases)
                                            )
                                        }
                                    }
                                )
                            ).project()
                        }
                    }
                }
            }
        }
    }
}
