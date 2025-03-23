import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.carbookingapp.MainActivity
import com.example.carbookingapp.R
class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameEditText: EditText = view.findViewById(R.id.username_edit_text)
        val passwordEditText: EditText = view.findViewById(R.id.password_edit_text)
        val loginButton: Button = view.findViewById(R.id.login_button)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // ตรวจสอบชื่อผู้ใช้และรหัสผ่าน (ตัวอย่างง่ายๆ)
            if (username == "user" && password == "password") {
                // หากถูกต้อง ให้ไปยัง MainActivity
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish() // ปิด Activity หลัก
            } else {
                // หากไม่ถูกต้อง ให้แสดงข้อความผิดพลาด
                Toast.makeText(requireContext(), "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
            }
        }
    }
}