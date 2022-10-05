package material.icons

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    Snackbar.make(
                        window.decorView.findViewById(android.R.id.content),
                        data?.getStringExtra("icon").toString(), Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

        findViewById<MaterialButton>(R.id.btn_select_icon).setOnClickListener {
            resultLauncher.launch(Intent(this, MaterialIconSelectorActivity::class.java))
        }
    }
}
