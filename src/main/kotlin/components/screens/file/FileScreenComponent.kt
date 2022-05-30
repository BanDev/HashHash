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

package components.screens.file

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.appmattus.crypto.Algorithm
import com.arkivanov.decompose.ComponentContext
import hash
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.File

class FileScreenComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    var comparisonHash by mutableStateOf("")
    var file: File? by mutableStateOf(null)
    var fileHash by mutableStateOf("")
    var fileHashJob: Job? by mutableStateOf(null)
    var hashProgress by mutableStateOf(0F)
    var instantBeforeHash: Instant? by mutableStateOf(null)
    var instantAfterHash: Instant? by mutableStateOf(null)
    private var mainFileException: Exception? by mutableStateOf(null)
    private var hashedTextUppercase by mutableStateOf(true)
    var algorithm: Algorithm by mutableStateOf(Algorithm.MD5)

    fun onCalculateClicked(scope: CoroutineScope) {
        if (fileHashJob?.isActive != true) {
            fileHashJob = scope.launch(Dispatchers.IO) {
                instantBeforeHash = Clock.System.now()
                try {
                    fileHash = file?.hash(
                        algorithm = algorithm,
                        hashProgressCallback = { hashProgress = it }
                    )?.run { if (hashedTextUppercase) uppercase() else lowercase() }.toString()
                    instantAfterHash = Clock.System.now()
                } catch (_: CancellationException) {
                } catch (exception: Exception) {
                    mainFileException = exception
                }
                fileHashJob = null
            }
        } else {
            hashProgress = 0F
            fileHashJob?.cancel()
            fileHashJob = null
        }
    }
}