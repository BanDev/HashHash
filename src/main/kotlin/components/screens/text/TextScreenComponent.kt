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

package components.screens.text

import com.appmattus.crypto.Algorithm
import com.arkivanov.decompose.ComponentContext

class TextScreenComponent(
    componentContext: ComponentContext,
    val algorithm: Algorithm,
    val givenTextHash: String,
    val textComparisonHash: String,
    val onUppercaseClick: () -> Unit,
    val onLowercaseClick: () -> Unit,
    val onClearTextClick: () -> Unit,
    val onComparisonClearClick: () -> Unit,
    val onCaseClick: () -> Unit,
    val onPasteClick: () -> Unit,
    val onComparisonTextFieldChange: (String) -> Unit
) : ComponentContext by componentContext {
    companion object {
        const val characterLimit = 20000
    }
}