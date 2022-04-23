package components.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.pushingpixels.aurora.component.model.Command
import org.pushingpixels.aurora.component.model.LabelContentModel
import org.pushingpixels.aurora.component.model.LabelPresentationModel
import org.pushingpixels.aurora.component.projection.CommandButtonProjection
import org.pushingpixels.aurora.component.projection.LabelProjection

@Composable
fun AttributionScreen(
    onGoBackClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LabelProjection(
                contentModel = LabelContentModel("Attribution"),
                presentationModel = LabelPresentationModel(
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            ).project()
            LazyColumn {
                items(attributionList) {
                    LabelProjection(contentModel = LabelContentModel(text = it)).project()
                }
            }
        }
        CommandButtonProjection(
            contentModel = Command(
                text = "Go back",
                action = onGoBackClicked
            )
        ).project()
    }
}

val attributionList = listOf(
    "Application Icon - Freepik via Flaticon",
    "File types icon pack - Smashicons via Flaticon",
    "Text Editor icon pack - Freepik via Flaticon",
    "Book Store icon pack - Freepik via Flaticon"
)