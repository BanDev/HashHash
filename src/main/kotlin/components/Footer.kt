package components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import helper.FileUtils
import org.pushingpixels.aurora.component.model.LabelContentModel
import org.pushingpixels.aurora.component.projection.LabelProjection
import org.pushingpixels.aurora.theming.DecorationAreaType
import org.pushingpixels.aurora.theming.auroraBackground
import org.pushingpixels.aurora.window.AuroraDecorationArea
import java.io.File

@Composable
fun Footer(
    error: String,
    hashedOutput: String,
    isHashing: Boolean,
    file: File
) {
    AuroraDecorationArea(decorationAreaType = DecorationAreaType.Footer) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxWidth()
                .auroraBackground()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            LabelProjection(
                contentModel = LabelContentModel(
                    text = when {
                        error.isNotBlank() -> error
                        hashedOutput.isNotBlank() -> "Done!"
                        isHashing -> "Hashing..."
                        file != FileUtils.emptyFile -> "No hash"
                        else -> "No file selected"
                    }
                )
            ).project()
        }
    }
}