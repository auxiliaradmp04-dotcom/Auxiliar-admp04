package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SystemQaBadge
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MasterOperationsViewModel
import kotlinx.coroutines.launch

enum class Screen(val title: String, val icon: ImageVector) {
    DASHBOARD("Tablero Maestro", Icons.Default.Dashboard),
    PROSPECTIVA_ESTRATEGICA("Radar & Futuro", Icons.Default.Radar),
    CRM("CRM & Ingreso", Icons.Default.People),
    LEGAL("Gestión Jurídica", Icons.Default.Gavel),
    FINANCE("Finanzas", Icons.Default.Payments),
    AGENDA("Agenda Inteligente", Icons.Default.CalendarMonth),
    EMPLOYMENT("Empleo & OIM", Icons.Default.Work),
    VISTA_360("Vista 360", Icons.Default.Search),
    SYSTEM_CONTROL("Auditoría & QA", Icons.Default.Verified)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MasterOperationsViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                // Listen for messages from ViewModel
                LaunchedEffect(Unit) {
                    viewModel.userMessage.collect { message ->
                        snackbarHostState.showSnackbar(message)
                    }
                }

                // Handle system back navigation
                if (currentScreen != Screen.DASHBOARD) {
                    BackHandler {
                        currentScreen = Screen.DASHBOARD
                    }
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.width(300.dp),
                            drawerContainerColor = NavyDark
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "PROSPECTIVA 0.5",
                                    color = GoldAccent,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Sistema Maestro de Operaciones",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 13.sp
                                )
                                SystemQaBadge(score = 100)
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Screen.entries.forEach { screen ->
                                NavigationDrawerItem(
                                    icon = { Icon(screen.icon, contentDescription = null, tint = if (currentScreen == screen) GoldAccent else Color.White) },
                                    label = { Text(screen.title, color = if (currentScreen == screen) GoldAccent else Color.White, fontWeight = FontWeight.SemiBold) },
                                    selected = currentScreen == screen,
                                    onClick = {
                                        currentScreen = screen
                                        scope.launch { drawerState.close() }
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = NavyCard,
                                        unselectedContainerColor = Color.Transparent
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp).testTag("drawer_item_${screen.name}")
                                )
                            }
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            TopAppBar(
                                title = {
                                    Column {
                                        Text(
                                            text = currentScreen.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Prospectiva 0.5 · S.A.S.",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                navigationIcon = {
                                    if (currentScreen != Screen.DASHBOARD) {
                                        IconButton(
                                            onClick = { currentScreen = Screen.DASHBOARD },
                                            modifier = Modifier.testTag("nav_back_button")
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar al Dashboard")
                                        }
                                    } else {
                                        IconButton(
                                            onClick = { scope.launch { drawerState.open() } },
                                            modifier = Modifier.testTag("nav_drawer_button")
                                        ) {
                                            Icon(Icons.Default.Menu, contentDescription = "Menú de Navegación")
                                        }
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { currentScreen = Screen.VISTA_360 }) {
                                        Icon(Icons.Default.Search, contentDescription = "Buscador 360", tint = GoldAccent)
                                    }
                                    IconButton(onClick = { currentScreen = Screen.SYSTEM_CONTROL }) {
                                        Icon(Icons.Default.Shield, contentDescription = "Control de Sistema", tint = SuccessGreen)
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                modifier = Modifier
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                                    .testTag("bottom_nav_bar"),
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 3.dp
                            ) {
                                val primaryScreens = listOf(
                                    Screen.DASHBOARD,
                                    Screen.PROSPECTIVA_ESTRATEGICA,
                                    Screen.CRM,
                                    Screen.LEGAL,
                                    Screen.FINANCE
                                )

                                primaryScreens.forEach { screen ->
                                    NavigationBarItem(
                                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                                        label = { Text(screen.title.split(" ").first(), fontSize = 11.sp) },
                                        selected = currentScreen == screen,
                                        onClick = { currentScreen = screen },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = NavyDark,
                                            selectedTextColor = NavyDark,
                                            indicatorColor = GoldAccent
                                        ),
                                        modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentScreen) {
                                Screen.DASHBOARD -> DashboardScreen(
                                    viewModel = viewModel,
                                    onNavigateToProspectiva = { currentScreen = Screen.PROSPECTIVA_ESTRATEGICA },
                                    onNavigateToCrm = { currentScreen = Screen.CRM },
                                    onNavigateToLegal = { currentScreen = Screen.LEGAL },
                                    onNavigateToFinance = { currentScreen = Screen.FINANCE },
                                    onNavigateToAgenda = { currentScreen = Screen.AGENDA },
                                    onNavigateToEmployment = { currentScreen = Screen.EMPLOYMENT },
                                    onNavigateTo360 = { currentScreen = Screen.VISTA_360 }
                                )
                                Screen.PROSPECTIVA_ESTRATEGICA -> ProspectivaScreen(viewModel = viewModel)
                                Screen.CRM -> CrmScreen(
                                    viewModel = viewModel,
                                    onClientSelected = { client ->
                                        viewModel.selectClientFor360(client)
                                        currentScreen = Screen.VISTA_360
                                    }
                                )
                                Screen.LEGAL -> LegalScreen(viewModel = viewModel)
                                Screen.FINANCE -> FinanceScreen(viewModel = viewModel)
                                Screen.AGENDA -> AgendaScreen(viewModel = viewModel)
                                Screen.EMPLOYMENT -> EmploymentScreen(viewModel = viewModel)
                                Screen.VISTA_360 -> Vista360Screen(viewModel = viewModel)
                                Screen.SYSTEM_CONTROL -> SystemControlScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
