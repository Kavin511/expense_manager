import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.devstudio.expensemanager.R


@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun HomeAppBar() {
    TopAppBar(title = { Text(stringResource(id = R.string.app_name)) }, actions = {
        HomeActions()
    })
}