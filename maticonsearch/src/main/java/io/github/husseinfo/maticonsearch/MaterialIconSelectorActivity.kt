package io.github.husseinfo.maticonsearch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.FilterList
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

class MaterialIconSelectorActivity : ComponentActivity() {

    private val viewModel by viewModels<MaterialIconSelectorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val icons by viewModel.icons.collectAsState()
            val query by viewModel.query.collectAsState()
            val searchFilters = viewModel.iconStyles

            MaterialTheme(colorScheme = getAppColorScheme(this, isSystemInDarkTheme())) {
                IconsList(
                    icons,
                    query,
                    viewModel::search,
                    searchFilters,
                    viewModel::filter,
                    this
                )
            }
        }
    }
}

const val ACTIVITY_RESULT_ICON_NAME = "icon"

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun IconsList(
    listOfIcons: List<ImageVector>,
    query: String?,
    onSearch: (String?) -> Unit,
    searchFilters: List<Any>,
    onFilter: (Any) -> Unit,
    activity: Activity
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { SearchTextField(query, onSearch) },
                navigationIcon = {
                    IconButton(onClick = { onSearch("") }) {
                        Icon(
                            imageVector = Icons.TwoTone.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                actions = { FiltersMenu(searchFilters = searchFilters, onFilter = onFilter) },
                scrollBehavior = scrollBehavior,
            )
        },
        content = { innerPadding ->
            LazyVerticalGrid(
                modifier = Modifier.padding(7.dp, 0.dp),
                contentPadding = innerPadding,
                columns = GridCells.Adaptive(123.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                items(listOfIcons) {
                    GridItem(it, activity)
                }
            }
        }
    )
}

@Composable
fun SearchTextField(
    query: String?,
    onSearch: (String?) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = query ?: "",
        onValueChange = onSearch,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface
        )
    )
    LaunchedEffect(Unit) {
        if (query.isNullOrEmpty()) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun FiltersMenu(
    searchFilters: List<Any>,
    onFilter: (Any) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val selectedFilter = remember { mutableStateOf(searchFilters[0]) }

    IconButton(onClick = { showMenu = !showMenu }) {
        Icon(
            imageVector = Icons.TwoTone.FilterList,
            contentDescription = "Filter"
        )
    }
    DropdownMenu(expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        searchFilters.forEach { filter ->
            DropdownMenuItem(onClick = {
                selectedFilter.value = filter
                onFilter(filter)
                showMenu = false
            }) {
                Row {
                    RadioButton(
                        selected = selectedFilter.value == filter,
                        onClick = {
                            selectedFilter.value = filter
                            onFilter(filter)
                            onFilter(filter)
                            showMenu = false
                        }
                    )
                }
                Text(text = filter.javaClass.simpleName, modifier = Modifier.padding(start = 7.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GridItem(icon: ImageVector, activity: Activity) {
    Card(backgroundColor = MaterialTheme.colorScheme.surfaceVariant, onClick = {
        activity.intent
        val intent = Intent()
        intent.putExtra(ACTIVITY_RESULT_ICON_NAME, icon.name)
        activity.setResult(Activity.RESULT_OK, intent)
        activity.finish()
    }) {
        Column(
            modifier = Modifier
                .height(99.dp)
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            var multiplier by remember { mutableStateOf(1f) }
            val iconName = icon.name.replaceBefore(".", "").replace(".", "")

            Icon(
                modifier = Modifier.size(40.dp),
                imageVector = icon,
                contentDescription = iconName
            )
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = iconName,
                maxLines = 1,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Visible,
                style = LocalTextStyle.current.copy(
                    fontSize = LocalTextStyle.current.fontSize * multiplier
                ),
                onTextLayout = {
                    if (it.hasVisualOverflow) {
                        multiplier *= 0.99f
                    }
                }
            )
        }
    }
}

fun getIcon(
    context: Context,
    iconName: String?,
    iconsStyle: Any?
): ImageVector {
    var iconsStyleOrFilled = iconsStyle
    if (iconsStyleOrFilled == null)
        iconsStyleOrFilled = Icons.Filled

    val className =
        context.classLoader.loadClass("androidx.compose.material.icons.${iconsStyleOrFilled.javaClass.simpleName.lowercase()}.${iconName}Kt")
    val methods = className.declaredMethods
    val getFun = methods.first()
    return getFun.invoke(null, iconsStyle) as ImageVector
}

fun getIconByName(context: Context, name: String): ImageVector {
    val iconName = name.split(".")[1]
    val iconStyle: Any = when (name.split(".")[0]) {
        "Filled" -> Icons.Filled
        "Outlined" -> Icons.Outlined
        "TwoTone" -> Icons.TwoTone
        "Rounded" -> Icons.Rounded
        else -> Icons.Filled
    }
    return getIcon(context, iconName, iconStyle)
}

fun getIconByName(context: Context, result: ActivityResult?): ImageVector {
    val data: Intent? = result?.data
    val name = data?.getStringExtra(ACTIVITY_RESULT_ICON_NAME).toString()
    return getIconByName(context, name)
}
