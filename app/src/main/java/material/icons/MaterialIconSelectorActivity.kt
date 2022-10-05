package material.icons

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.FilterList
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material.icons.twotone.Wifi
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import material.icons.ui.theme.MaterialIconsTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MaterialIconSelectorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        setContent {
            val icons by viewModel.icons.collectAsState()
            val query by viewModel.query.collectAsState()
            val searchFilters = viewModel.iconStyles

            MaterialIconsTheme {
                IconsList(icons, query, viewModel::search, searchFilters, viewModel::filter)
            }
        }
    }

    companion object {
        val TAG = MainActivity::class.simpleName
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun IconsList(
    listOfIcons: List<ImageVector>,
    query: String?,
    onSearch: (String?) -> Unit,
    searchFilters: List<Any>,
    onFilter: (Any) -> Unit
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
                    GridItem(it)
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
                            showMenu = false
                        }
                    )
                }
                Text(text = filter.javaClass.simpleName, modifier = Modifier.padding(start = 7.dp))
            }
        }
    }
}

@Composable
fun GridItem(icon: ImageVector) {
    Card(backgroundColor = MaterialTheme.colorScheme.surfaceVariant) {
        Column(
            modifier = Modifier
                .height(99.dp)
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialIconsTheme {
        IconsList(listOf(Icons.TwoTone.Wifi), "", {}, listOf(), {})
    }
}
