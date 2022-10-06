package material.icons

import android.app.Application
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.lang.reflect.Field

class MaterialIconSelectorViewModel(application: Application) : AndroidViewModel(application) {

    val iconStyles = listOf(Icons.Filled, Icons.Outlined, Icons.Rounded, Icons.Sharp, Icons.TwoTone)

    private val _query = MutableStateFlow(null as String?)
    val query: StateFlow<String?> = _query

    private val _filters = MutableStateFlow(iconStyles[0])
    private val filters: StateFlow<Any> = _filters

    private val _isLoading = MutableStateFlow(true)

    val icons: StateFlow<List<ImageVector>> = query
        .combine(filters) { query, filters ->
            getIcons(query, filters)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ArrayList()
        )

    fun search(query: String?) {
        _query.value = query
    }

    fun filter(filters: Any) {
        _filters.value = filters
    }

    private suspend fun getIcons(iconName: String?, iconsStyle: Any): List<ImageVector> {
        _isLoading.value = true
        val icons = queryIcons(getApplication() as Context, iconName, iconsStyle)
        _isLoading.value = false
        return icons
    }
}


private fun field(className: String, fieldName: String): Field {
    val clazz = Class.forName(className)
    val field = clazz.getDeclaredField(fieldName)
    field.isAccessible = true
    return field
}

@Suppress("UNCHECKED_CAST")
private fun getDexFiles(context: Context): Sequence<DexFile> {
    val classLoader = context.classLoader as BaseDexClassLoader

    val pathListField = field("dalvik.system.BaseDexClassLoader", "pathList")
    val pathList = pathListField.get(classLoader)

    val dexElementsField = field("dalvik.system.DexPathList", "dexElements")
    val dexElements = dexElementsField.get(pathList) as Array<Any>

    val dexFileField = field("dalvik.system.DexPathList\$Element", "dexFile")
    return dexElements.map { dexFileField.get(it) as DexFile }.asSequence()
}

public suspend fun queryIcons(
    context: Context,
    iconName: String?,
    iconsStyle: Any
): List<ImageVector> =
    withContext(Dispatchers.IO) {
        val icons = ArrayList<ImageVector>()
        val classes = getDexFiles(context)
            .flatMap { it.entries().asSequence() }
            .filter { it.startsWith("androidx.compose.material.icons.${iconsStyle.javaClass.simpleName.lowercase()}") }
            .map { context.classLoader.loadClass(it) }

        for (cls in classes) {
            val methods = cls.declaredMethods
            val getFun = methods.first()

            val image = getFun.invoke(null, iconsStyle)

            if (image is ImageVector && (iconName.isNullOrEmpty() || image.name.contains(
                    iconName,
                    true
                ))
            )
                icons.add(image)
        }
        icons
    }
