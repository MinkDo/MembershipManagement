import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.example.membershipmanagement.R
import com.example.membershipmanagement.navigation.Screen
import com.example.membershipmanagement.viewmodel.ProfileViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

@Composable
fun EditProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val updateResult by profileViewModel.updateResult.collectAsState() // ✅ Lắng nghe kết quả API
    val uiState by profileViewModel.profileState.collectAsState()
    val context = LocalContext.current

    var fullName by remember { mutableStateOf(uiState.userData?.fullName ?: "") }
    var dateOfBirth by remember { mutableStateOf(uiState.userData?.profile?.dateOfBirth ?: "") }
    var gender by remember { mutableStateOf(uiState.userData?.profile?.gender?.toString() ?: "0") }
    var currentRank by remember { mutableStateOf(uiState.userData?.profile?.currentRank?.toString() ?: "0") }
    var avatarUri by remember { mutableStateOf(profileViewModel.getAvatarUrl()) }
    var phoneNumber by remember { mutableStateOf(uiState.userData?.phoneNumber ?: "") }
    var address by remember { mutableStateOf(uiState.userData?.profile?.address ?: "") }
    var joinDate by remember { mutableStateOf(uiState.userData?.profile?.joinDate ?: "") }

    // Chọn ảnh từ thư viện
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            avatarUri =  it.toString()
            profileViewModel.setAvatarUrl(it.toString())  // ✅ Cập nhật Avatar trong ViewModel
        }
    }

    Scaffold(
        topBar = { EditProfileTopBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Ảnh đại diện
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Log.d("EditProfileScreen","Image: $avatarUri")
                    Image(

                        painter = if (avatarUri.isNotEmpty()) rememberImagePainter(avatarUri)
                        else painterResource(id = R.drawable.avatar),
                        contentDescription = "Ảnh đại diện",
                        modifier = Modifier.fillMaxSize()
                    )
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Chọn ảnh",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Họ và tên
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Họ và tên") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Số điện thoại
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
                )

                // Địa chỉ
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Địa chỉ") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Ngày sinh (DatePicker)
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    label = { Text("Ngày sinh") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = { showDatePicker(context) { dateOfBirth = it } }
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar")
                        }
                    }
                )
                // Ngày sinh (DatePicker)
                OutlinedTextField(
                    value = joinDate,
                    onValueChange = { joinDate = it },
                    label = { Text("Ngày tham gia") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = { showDatePicker(context) { joinDate = it } }
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar")
                        }
                    }
                )


                // Giới tính
                DropdownField(
                    label = "Giới tính",
                    options = listOf("Nam", "Nữ"),
                    selectedOption = if (gender == "0") "Nam" else "Nữ",
                    onOptionSelected = { gender = if (it == "Nam") "0" else "1" }
                )

                // Cấp đai
                val rankOptions = listOf("Trắng", "Vàng", "Xanh", "Đỏ", "Đen")
                DropdownField(
                    label = "Cấp đai",
                    options = rankOptions,
                    selectedOption = rankOptions.getOrNull(currentRank.toInt()) ?: "Trắng",
                    onOptionSelected = { currentRank = rankOptions.indexOf(it).toString() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val avatarFile = avatarUri.takeIf { it.isNotEmpty() }?.let {
                            getFileFromUri(context, Uri.parse(it)) // ✅ Chuyển đổi URI thành File
                        }

                        profileViewModel.updateProfile(
                            id = profileViewModel.getUserId(),
                            phoneNumber = phoneNumber,
                            fullName = fullName,
                            avatarFile = avatarFile, // ✅ Gửi File nếu có, null nếu không
                            avatarUrl = avatarUri,
                            gender = gender.toInt(),
                            dateOfBirth = dateOfBirth,
                            address = address,
                            currentRank = currentRank.toInt(),
                            joinDate = joinDate
                        )
                        Log.d("EditProfileScreen", "Update Request")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lưu thay đổi")
                }

                // Nếu cập nhật thành công, quay về màn hình trước
                LaunchedEffect(updateResult) {
                    updateResult?.let {
                        if (it.isSuccess) {
                            profileViewModel.resetUpdateResult()
                            navController.popBackStack()
                        } else {
                            Log.e("EditProfileScreen", "Lỗi cập nhật: ${it.exceptionOrNull()?.message}")
                        }
                    }
                }

                //  Hiển thị thông báo lỗi nếu có
                updateResult?.exceptionOrNull()?.message?.let { errorMessage ->
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                }

            }
        }
    }
}

//  Hiển thị DatePicker theo thứ tự NĂM - THÁNG - NGÀY
fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
        onDateSelected("$selectedYear-${selectedMonth + 1}-$selectedDay")
    }, year, month, day).show()
}

//  Sửa lỗi Dropdown hiển thị đúng giá trị
@Composable
fun DropdownField(label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Button(onClick = { expanded = true }) {
            Text(selectedOption)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Chỉnh sửa hồ sơ", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}


fun getFileFromUri(context: Context, uri: Uri): File? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_avatar.jpg")

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}