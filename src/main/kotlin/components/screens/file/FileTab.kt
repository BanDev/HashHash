/**

HashHash
Copyright (C) 2023 Russell Banks

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

package components.screens.file

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.DragData
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.onExternalDrag
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import components.ComparisonTextFieldRow
import components.ElapsedTimeResults
import components.HashProgress
import components.OutputTextFieldRow
import components.screens.ParentComponent
import java.io.File
import java.net.URI
import org.pushingpixels.aurora.component.model.LabelContentModel
import org.pushingpixels.aurora.component.model.LabelPresentationModel
import org.pushingpixels.aurora.component.projection.HorizontalSeparatorProjection
import org.pushingpixels.aurora.component.projection.LabelProjection
import org.pushingpixels.aurora.theming.AuroraSkin
import tv.wunderbox.nfd.FileDialog

class FileTab(private val fileDialog: FileDialog) : Tab {
    override val options: TabOptions
        @Composable
        get() = remember {
            TabOptions(
                index = 0u,
                title = "File",
            )
        }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val backgroundColorScheme = AuroraSkin.colors.getBackgroundColorScheme(
            decorationAreaType = AuroraSkin.decorationAreaType
        )
        Column(
            modifier = Modifier
                .padding(16.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(6.dp))
                .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 16.dp)
                .onExternalDrag { externalDragValue ->
                    val dragData = externalDragValue.dragData
                    if (dragData is DragData.FilesList) {
                        FileScreenModel.setComponentFile(File(URI(dragData.readFiles().first())))
                    }
                },
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SelectFileRow { FileScreenModel.selectFile(fileDialog) }
            FileInfoRow(FileScreenModel.file)
            HashButton(FileScreenModel.file, FileScreenModel.fileHashJob, FileScreenModel::onCalculateClicked)
            HorizontalSeparatorProjection().project(Modifier.fillMaxWidth())
            OutputTextFieldRow(
                value = FileScreenModel.resultMap.getOrDefault(ParentComponent.algorithm, ""),
                isValueUppercase = FileScreenModel.hashedTextUppercase,
                onCaseClick = FileScreenModel::switchHashCase
            )
            HorizontalSeparatorProjection().project(Modifier.fillMaxWidth())
            val clipboardManager = LocalClipboardManager.current
            ComparisonTextFieldRow(
                hashedOutput = FileScreenModel.resultMap.getOrDefault(ParentComponent.algorithm, ""),
                comparisonHash = FileScreenModel.comparisonHash,
                onPasteClick = {
                    FileScreenModel.comparisonHash = (clipboardManager.getText()?.text.orEmpty()).filterNot(Char::isWhitespace)
                },
                onClearClick = { FileScreenModel.comparisonHash = "" },
                onTextFieldChange = { FileScreenModel.comparisonHash = it.filterNot(Char::isWhitespace) }
            )
            HorizontalSeparatorProjection().project(Modifier.fillMaxWidth())
            HashProgress(fileHashProgress = FileScreenModel.hashProgress, timer = FileScreenModel.timer)
            HorizontalSeparatorProjection().project(Modifier.fillMaxWidth())
            ElapsedTimeResults(
                instantBeforeHash = FileScreenModel.instantBeforeHash,
                instantAfterHash = FileScreenModel.instantAfterHash
            )
        }
        LabelProjection(
            contentModel = LabelContentModel(text = "Single file hashing"),
            presentationModel = LabelPresentationModel(textStyle = TextStyle(fontSize = 18.sp))
        ).project(
            Modifier.padding(horizontal = 32.dp, vertical = 3.dp).background(backgroundColorScheme.backgroundFillColor)
        )
    }
}
