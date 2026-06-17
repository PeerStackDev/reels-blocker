import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reelsblocker.ui.theme.ACCENT
import com.example.reelsblocker.ui.theme.GRAY
import com.example.reelsblocker.ui.theme.SUB_BACKGROUND
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController

//@Preview()
@Composable
fun FooterNav(navController: NavHostController) {

    var selectedItem by remember {
        mutableIntStateOf(0)
    }

    NavigationBar(
        modifier = Modifier.clip(
            RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp
            )
        ),
        containerColor = SUB_BACKGROUND
    ) {

        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = {
                selectedItem = 0
                navController.navigate("main")
            },
            icon = {
                Icon(Icons.Default.Home, contentDescription = null)
            },
            label = { Text("Главная") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ACCENT,
                selectedTextColor = ACCENT,
                unselectedIconColor = GRAY,
                unselectedTextColor = GRAY,
                indicatorColor = SUB_BACKGROUND
            )
        )

        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = {
                selectedItem = 1
                navController.navigate("settings")
            },
            icon = {
                Icon(Icons.Default.Settings, contentDescription = null)
            },
            label = { Text("Настройки") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ACCENT,
                selectedTextColor = ACCENT,
                unselectedIconColor = GRAY,
                unselectedTextColor = GRAY,
                indicatorColor = SUB_BACKGROUND
            )
        )

        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = {
                selectedItem = 2
                navController.navigate("info")
            },
            icon = {
                Icon(Icons.Default.Info, contentDescription = null)
            },
            label = { Text("О приложении")},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ACCENT,
                selectedTextColor = ACCENT,
                unselectedIconColor = GRAY,
                unselectedTextColor = GRAY,
                indicatorColor = SUB_BACKGROUND
            )

        )
    }
}

