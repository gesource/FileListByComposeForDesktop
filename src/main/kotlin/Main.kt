import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.Component
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import javax.swing.JFileChooser


@ExperimentalComposeUiApi
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "FileList"
    ) {
        val model = FileListModel()
        DropTarget(this) { file ->
            model.dir = if (file.isDirectory) {
                file.absolutePath
            } else {
                file.parent
            }
        }
        FileListContent(model)
    }
}

fun selectDirectory(parent: Component? = null, currentDirectory: String? = null): String? {
    val fileChooser = JFileChooser()
    currentDirectory?.let {
        fileChooser.currentDirectory = File(currentDirectory)
    }
    fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
        return fileChooser.selectedFile.absolutePath
    }
    return null
}

/**
 * ウィンドウにファイルがドロップされたとき、onDropFile()関数を実行する
 * @param frameWindowScope ウィンドウ
 * @param onDropFile 実行する関数
 */
@Composable
fun DropTarget(
    frameWindowScope: FrameWindowScope,
    onDropFile: (File) -> Unit
) {
    LaunchedEffect(Unit) {
        frameWindowScope.window.dropTarget = DropTarget().apply {
            addDropTargetListener(object : DropTargetAdapter() {
                override fun drop(event: DropTargetDropEvent) {
                    event.acceptDrop(DnDConstants.ACTION_COPY)
                    val fileList = event.transferable.getTransferData(DataFlavor.javaFileListFlavor) as? List<*>
                    fileList?.let {
                        if (it.isNotEmpty()) {
                            val file = it[0] as? File
                            file?.let { onDropFile(file) }
                        }
                    }
                }
            })
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun FileListContent(model: FileListModel) {
    DesktopMaterialTheme {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(2.dp)
        ) {
            DirectorySelection(model) { dir ->
                model.changeDirectory(dir)
            }
            Box(modifier = Modifier.fillMaxHeight()) {
                val state = rememberLazyListState()

                LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state) {
                    items(model.files) { file ->
                        FileRow(file)
                        Divider()
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(scrollState = state)
                )
            }
        }
    }
}

@Composable
fun FileRow(file: File) {
    Row {
        if (file.isDirectory) {
            Icon(Icons.Filled.List, contentDescription = "File")
        } else {
            Icon(Icons.Filled.Edit, contentDescription = "File")
        }
        Text(text = file.name)
    }
}

@ExperimentalComposeUiApi
@Composable
private fun DirectorySelection(model: FileListModel, onSelectDirectory: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        TextField(
            value = model.dir,
            onValueChange = { model.dir = it },
            label = { Text(text = "Directory") },
            singleLine = true,
            modifier = Modifier.weight(1f).onPreviewKeyEvent {
                if (it.key == Key.Enter) {
                    val file = File(model.dir)
                    if (file.exists() && file.isDirectory) {
                        onSelectDirectory(model.dir)
                    }
                    true
                } else {
                    false
                }
            }
        )
        Button(
            onClick = {
                val selectedDirectory = selectDirectory(currentDirectory = model.dir)
                selectedDirectory?.let { onSelectDirectory(it) }
            },
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(text = "Select")
        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
private fun PreviewFileListContent() {
    val model = FileListModel()
    FileListContent(model)
}
