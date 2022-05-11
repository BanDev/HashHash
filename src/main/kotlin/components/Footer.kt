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

package components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import org.pushingpixels.aurora.component.model.LabelContentModel
import org.pushingpixels.aurora.component.model.ProgressDeterminateContentModel
import org.pushingpixels.aurora.component.projection.DeterminateLinearProgressProjection
import org.pushingpixels.aurora.component.projection.LabelProjection
import org.pushingpixels.aurora.theming.DecorationAreaType
import org.pushingpixels.aurora.theming.auroraBackground
import org.pushingpixels.aurora.window.AuroraDecorationArea
import java.io.File

@Composable
fun Footer(
    error: Exception?,
    hashedOutput: String,
    job: Job?,
    hashProgress: Float,
    file: File?
) {
    AuroraDecorationArea(decorationAreaType = DecorationAreaType.Footer) {
        Box(Modifier.fillMaxWidth().auroraBackground().padding(horizontal = 8.dp, vertical = 6.dp)) {
            Crossfade(targetState = job?.isActive ?: false) { isHashing ->
                if (isHashing) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        DeterminateLinearProgressProjection(
                            contentModel = ProgressDeterminateContentModel(
                                progress = hashProgress
                            )
                        ).project(Modifier.fillMaxWidth().padding(4.dp))
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        LabelProjection(
                            contentModel = LabelContentModel(
                                text = when {
                                    error != null -> error.localizedMessage.replaceFirstChar { it.titlecase() }
                                    hashedOutput.isNotBlank() -> "Done!"
                                    file != null -> "No hash"
                                    else -> "No file selected"
                                }
                            )
                        ).project()
                    }
                }
            }
        }
    }
}
