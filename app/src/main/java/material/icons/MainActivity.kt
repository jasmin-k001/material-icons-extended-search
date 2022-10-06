package material.icons

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private var _binding: MainActivity? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val name = data?.getStringExtra("icon").toString()
                    val iconStyle = name.split(".")[0].lowercase()
                    val iconName = name.split(".")[1]
                    Snackbar.make(
                        window.decorView.findViewById(android.R.id.content),
                        name, Snackbar.LENGTH_SHORT
                    ).show()


                    findViewById<ComposeView>(R.id.compose_view).setContent {
                        MaterialTheme {
                            Surface {
                                Icon(
                                    modifier = Modifier.size(40.dp),
                                    imageVector = getIcon(
                                        baseContext,
                                        iconName,
                                        iconStyle
                                    ),
                                    contentDescription = name
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
