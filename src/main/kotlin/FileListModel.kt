import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.File

class FileListModel {
    var dir by mutableStateOf("")
    var files = mutableStateListOf<File>()

    fun changeDirectory(dir: String) {
        val newDir = File(dir)
        if (!newDir.isDirectory) {
            return
        }
        this.dir = dir
        this.files.clear()

        val comparator: Comparator<File> =
            Comparator { a, b ->
                if ((a.isDirectory && b.isDirectory) || (a.isFile && b.isFile)) {
                    a.name.compareTo(b.name, true)
                } else if (a.isDirectory) {
                    -1
                } else {
                    1
                }
            }
        newDir.listFiles()?.filter { file -> !file.isHidden }?.sortedWith(comparator)?.onEach { file -> files.add(file) }
    }
}