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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import koin.inject
import org.pushingpixels.aurora.component.model.TabContentModel
import org.pushingpixels.aurora.component.model.TabsContentModel
import org.pushingpixels.aurora.component.model.TabsPresentationModel
import org.pushingpixels.aurora.component.projection.TabsProjection
import org.pushingpixels.aurora.theming.TabContentSeparatorKind

@Composable
fun Tabs() {
    val root: Root by inject()
    TabsProjection(
        contentModel = TabsContentModel(
            tabs = listOf(
                TabContentModel(text = "File"),
                TabContentModel(text = "Text"),
                TabContentModel(text = "Compare Files")
            ),
            selectedTabIndex = root.routerState.subscribeAsState().value.activeChild.instance.toInt(),
            onTriggerTabSelected = {
                when (it) {
                    0 -> root.onFileTabClicked()
                    1 -> root.onTextTabClicked()
                    2 -> root.onCompareFilesTabClicked()
                }
            }
        ),
        presentationModel = TabsPresentationModel(
            leadingMargin = 30.dp,
            trailingMargin = 30.dp,
            contentSeparatorKind = TabContentSeparatorKind.Single,
            tabContentPadding = PaddingValues(horizontal = 30.dp, vertical = 6.dp),
        )
    ).project()
}
