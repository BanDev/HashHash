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

package components.screens.compare

import Hashing
import Hashing.hash
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.appmattus.crypto.Algorithm
import com.hoc081098.flowext.interval
import components.HashProgress
import components.OutputTextFieldRow
import components.Timer
import components.screens.ParentComponent
import helper.FileUtils
import helper.Icons
import io.klogging.Klogging
import java.io.File
import java.nio.file.Files
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import org.pushingpixels.aurora.component.model.Command
import org.pushingpixels.aurora.component.model.CommandButtonPresentationModel
import org.pushingpixels.aurora.component.model.LabelContentModel
import org.pushingpixels.aurora.component.projection.CommandButtonProjection
import org.pushingpixels.aurora.component.projection.HorizontalSeparatorProjection
import org.pushingpixels.aurora.component.projection.LabelProjection
import org.pushingpixels.aurora.theming.IconFilterStrategy
import tv.wunderbox.nfd.FileDialog
import tv.wunderbox.nfd.FileDialogResult
import java.awt.Component

object CompareFilesModel : ScreenModel, Klogging {
    val algorithm = ParentComponent.algorithm
    var fileOne: File? by mutableStateOf(null)
    private var fileOneHashUppercase by mutableStateOf(true)
    private var fileOneHashProgress by mutableStateOf(0F)
    private var fileOneTimer by mutableStateOf(Timer(minutes = 0L, seconds = 0L))
    private var fileOneResultMap: SnapshotStateMap<Algorithm, String> = mutableStateMapOf()

    var fileTwo: File? by mutableStateOf(null)
    private var fileTwoHashUppercase by mutableStateOf(true)
    private var fileTwoHashProgress by mutableStateOf(0F)
    private var fileTwoTimer by mutableStateOf(Timer(minutes = 0L, seconds = 0L))
    private var fileTwoResultMap: SnapshotStateMap<Algorithm, String> = mutableStateMapOf()

    private var comparisonJobList: List<Deferred<Unit>>? by mutableStateOf(null)

    val footerText: String get() = when {
        fileOne == null && fileTwo == null -> "No files selected"
        fileOne == null && fileTwo != null -> "1st file not selected"
        fileOne != null && fileTwo == null -> "2nd file not selected"
        fileOneResultMap[algorithm]?.isBlank() == true && fileTwoResultMap[algorithm]?.isBlank() == true -> {
            "No hashes"
        }
        fileOneResultMap[algorithm]?.isBlank() == true && fileTwoResultMap[algorithm]?.isNotBlank() == true -> {
            "No hash for 1st file"
        }
        fileOneResultMap[algorithm]?.isNotBlank() == true && fileTwoResultMap[algorithm]?.isBlank() == true -> {
            "No hash for 2nd file"
        }
        else -> ""
    }

    val doHashesMatch get() = fileOneResultMap[algorithm].equals(fileTwoResultMap[algorithm], ignoreCase = true)

    fun onCalculateClicked() {
        if ((comparisonJobList?.count(Deferred<Unit>::isActive) ?: 0) <= 0) {
            coroutineScope.launch(Dispatchers.Default) {
                comparisonJobList = listOf(
                    async(Dispatchers.IO) {
                        Hashing.catchFileHashingExceptions {
                            fileOneResultMap[algorithm] = fileOne?.hash(
                                algorithm = algorithm,
                                hashProgressCallback = { fileOneHashProgress = it }
                            )?.run { if (fileOneHashUppercase) uppercase() else lowercase() }.orEmpty()
                        }
                    },
                    async(Dispatchers.IO) {
                        Hashing.catchFileHashingExceptions {
                            fileTwoResultMap[algorithm] = fileTwo?.hash(
                                algorithm = algorithm,
                                hashProgressCallback = { fileTwoHashProgress = it }
                            )?.run { if (fileTwoHashUppercase) uppercase() else lowercase() }.orEmpty()
                        }
                    }
                )
                coroutineScope.launch(Dispatchers.Default) {
                    interval(Duration.ZERO, 1.seconds)
                        .takeWhile { comparisonJobList?.first()?.isActive == true }
                        .collect {
                            fileOneTimer = Timer(
                                minutes = it.toDuration(DurationUnit.SECONDS).inWholeMinutes,
                                seconds = it
                            )
                        }
                }
                coroutineScope.launch(Dispatchers.Default) {
                    interval(Duration.ZERO, 1.seconds)
                        .takeWhile { comparisonJobList?.get(1)?.isActive == true }
                        .collect {
                            fileTwoTimer = Timer(
                                minutes = it.toDuration(DurationUnit.SECONDS).inWholeMinutes,
                                seconds = it
                            )
                        }
                }
                comparisonJobList?.awaitAll()
                comparisonJobList = null
            }
        } else {
            comparisonJobList?.forEach(Deferred<Unit>::cancel)
            comparisonJobList = null
            fileOneHashProgress = 0F
            fileTwoHashProgress = 0F
        }
    }

    fun onAlgorithmClick(algorithm: Algorithm) {
        fileOneResultMap[algorithm]?.run {
            fileOneResultMap[algorithm] = if (fileOneHashUppercase) uppercase() else lowercase()
        }
        fileTwoResultMap[algorithm]?.run {
            fileTwoResultMap[algorithm] = if (fileTwoHashUppercase) uppercase() else lowercase()
        }
    }

    fun areJobsActive() = (comparisonJobList?.count(Deferred<Unit>::isActive) ?: 0) <= 0

    private fun selectFile(fileComparison: FileComparison, fileDialog: FileDialog) {
        val result = fileDialog.pickFile()
        if (result is FileDialogResult.Success) {
            if (fileComparison == FileComparison.One) {
                fileOneResultMap.clear()
                fileOne = result.value
            } else if (fileComparison == FileComparison.Two) {
                fileTwoResultMap.clear()
                fileTwo = result.value
            }
        }
    }

    fun setDroppedFile(fileComparison: FileComparison, file: File?) {
        if (fileComparison == FileComparison.One) {
            fileOneResultMap.clear()
            fileOne = file
        } else if (fileComparison == FileComparison.Two) {
            fileTwoResultMap.clear()
            fileTwo = file
        }
    }

    private fun switchHashCase(fileComparison: FileComparison) {
        if (fileComparison == FileComparison.One) {
            fileOneHashUppercase = !fileOneHashUppercase
            fileOneResultMap[algorithm]?.run {
                fileOneResultMap[algorithm] = if (fileOneHashUppercase) uppercase() else lowercase()
            }
        } else if (fileComparison == FileComparison.Two) {
            fileTwoHashUppercase = !fileTwoHashUppercase
            fileTwoResultMap[algorithm]?.run {
                fileTwoResultMap[algorithm] = if (fileTwoHashUppercase) uppercase() else lowercase()
            }
        }
    }

    fun areHashesNotBlank(): Boolean {
        return fileOneResultMap[algorithm]?.isNotBlank() == true && fileTwoResultMap[algorithm]?.isNotBlank() == true
    }

    enum class FileComparison {
        One,
        Two
    }

    @Composable
    fun FileComparisonColumn(modifier: Modifier = Modifier, fileComparison: FileComparison, fileDialog: FileDialog) {
        if (fileComparison == FileComparison.One) {
            FileComparisonColumn(
                modifier = modifier,
                file = fileOne,
                fileResultMap = fileOneResultMap,
                isHashUppercase = fileOneHashUppercase,
                fileComparison = fileComparison,
                fileDialog = fileDialog
            )
        } else if (fileComparison == FileComparison.Two) {
            FileComparisonColumn(
                modifier = modifier,
                file = fileTwo,
                fileResultMap = fileTwoResultMap,
                isHashUppercase = fileTwoHashUppercase,
                fileComparison = fileComparison,
                fileDialog = fileDialog
            )
        }
    }

    @Composable
    private fun FileComparisonColumn(
        modifier: Modifier = Modifier,
        file: File?,
        fileResultMap: SnapshotStateMap<Algorithm, String>,
        isHashUppercase: Boolean,
        fileComparison: FileComparison,
        fileDialog: FileDialog
    ) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CommandButtonProjection(
                contentModel = Command(
                    text = "Select file",
                    icon = Icons.Utility.folderOpen(),
                    action = { selectFile(fileComparison, fileDialog) }
                ),
                presentationModel = CommandButtonPresentationModel(
                    iconEnabledFilterStrategy = IconFilterStrategy.ThemedFollowText,
                    iconActiveFilterStrategy = IconFilterStrategy.ThemedFollowText,
                    textStyle = TextStyle(textAlign = TextAlign.Center)
                )
            ).project()
            SelectionContainer {
                Column {
                    Icons.SystemIcon(modifier = Modifier.size(60.dp), file = file)
                    Spacer(Modifier.height(10.dp))
                    LabelProjection(
                        contentModel = LabelContentModel(
                            text = "Type: ${if (file != null) Files.probeContentType(file.toPath()) else ""}"
                        )
                    ).project()
                    LabelProjection(
                        contentModel = LabelContentModel(text = "Extension: ${file?.extension.orEmpty()}")
                    ).project()
                    LabelProjection(
                        contentModel = LabelContentModel(
                            text = "Size: ${if (file != null) FileUtils.getFormattedBytes(file.length()) else ""}"
                        )
                    ).project()
                    LabelProjection(
                        contentModel = LabelContentModel(text = "Path: ${file?.absolutePath.orEmpty()}")
                    ).project()
                }
            }
            HorizontalSeparatorProjection().project(Modifier.fillMaxWidth())
            OutputTextFieldRow(
                value = fileResultMap.getOrDefault(algorithm, ""),
                isValueUppercase = isHashUppercase,
                onCaseClick = { switchHashCase(fileComparison) }
            )
            HorizontalSeparatorProjection().project(Modifier.fillMaxWidth())
            HashProgress(fileHashProgress = fileOneHashProgress, timer = fileOneTimer)
        }
    }
}
