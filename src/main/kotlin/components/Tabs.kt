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

package components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import components.screens.compare.CompareFilesTab
import components.screens.file.FileTab
import components.screens.text.TextTab
import org.pushingpixels.aurora.component.model.TabContentModel
import org.pushingpixels.aurora.component.model.TabsContentModel
import org.pushingpixels.aurora.component.model.TabsPresentationModel
import org.pushingpixels.aurora.component.projection.TabsProjection
import org.pushingpixels.aurora.theming.TabContentSeparatorKind

@Composable
fun Tabs(
    tabNavigator: TabNavigator = LocalTabNavigator.current,
    fileTab: FileTab,
    textTab: TextTab,
    compareFilesTab: CompareFilesTab
) {
    TabsProjection(
        contentModel = TabsContentModel(
            tabs = listOf(
                TabContentModel(text = fileTab.options.title),
                TabContentModel(text = textTab.options.title),
                TabContentModel(text = compareFilesTab.options.title)
            ),
            selectedTabIndex = tabNavigator.current.options.index.toInt(),
            onTriggerTabSelected = {
                when (it) {
                    0 -> tabNavigator.current = fileTab
                    1 -> tabNavigator.current = textTab
                    2 -> tabNavigator.current = compareFilesTab
                }
            }
        ),
        presentationModel = TabsPresentationModel(
            leadingMargin = 30.dp,
            trailingMargin = 30.dp,
            contentSeparatorKind = TabContentSeparatorKind.Single,
            tabContentPadding = PaddingValues(horizontal = 30.dp, vertical = 6.dp)
        )
    ).project()
}
