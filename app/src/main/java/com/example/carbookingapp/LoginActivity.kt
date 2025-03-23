import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carbookingapp.R
import com.example.carbookingapp.MainActivity
 class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameEditText: EditText = findViewById(R.id.username_edit_text)
        val passwordEditText: EditText = findViewById(R.id.password_edit_text)
        val loginButton: Button = findViewById(R.id.login_button)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // ตรวจสอบชื่อผู้ใช้และรหัสผ่าน (ตัวอย่างง่ายๆ)
            if (username == "user" && password == "password") {
                // หากถูกต้อง ให้ไปยัง MainActivity
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish() // ปิด LoginActivity
            } else {
                // หากไม่ถูกต้อง ให้แสดงข้อความผิดพลาด
                Toast.makeText(this, "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
            }
        }
    }
}