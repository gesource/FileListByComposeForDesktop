import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class FileListModel {
    var dir by mutableStateOf("")
    var files = mutableStateListOf<String>()
}