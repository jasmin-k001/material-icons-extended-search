package material.icons

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import io.github.husseinfo.maticonsearch.MaterialIconSelectorActivity
import io.github.husseinfo.maticonsearch.getAppColorScheme
import io.github.husseinfo.maticonsearch.getIconByName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {

                    val icon = getIconByName(this, result)

                    Snackbar.make(
                        window.decorView.findViewById(android.R.id.content),
                        icon.name, Snackbar.LENGTH_SHORT
                    ).show()

                    findViewById<ComposeView>(R.id.compose_view).setContent {
                        MaterialTheme(
                            colorScheme = getAppColorScheme(
                                this,
                                isSystemInDarkTheme()
                            )
                        ) {
                            Surface {
                                Icon(
                                    modifier = Modifier.size(40.dp),
                                    imageVector = icon,
                                    contentDescription = icon.name
                                )
                            }
                        }
                    }
                }
            }

        findViewById<MaterialButton>(R.id.btn_select_icon).setOnClickListener {
            resultLauncher.launch(Intent(this, MaterialIconSelectorActivity::class.java))
        }
    }
}
