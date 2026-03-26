package com.example.bankapp.temp

/*

ocess: com.example.bankapp, PID: 29953
                                                                                                    java.lang.IllegalStateException: Restoring the Navigation back stack failed: destination 2027439596 cannot be found from the current destination NavGraph(0x78da56c6) route=main startDestination=home
                                                                                                    	at androidx.navigation.NavController.onGraphCreated(NavController.kt:1128)
                                                                                                    	at androidx.navigation.NavController.setGraph(NavController.kt:1086)
                                                                                                    	at androidx.navigation.NavController.setGraph(NavController.kt:100)
                                                                                                    	at androidx.navigation.compose.NavHostKt.NavHost(NavHost.kt:118)
                                                                                                    	at androidx.navigation.compose.NavHostKt.NavHost(NavHost.kt:67)
                                                                                                    	at com.example.bankapp.ui.components.navigators.AppNavHostKt.AppNavHost(AppNavHost.kt:58)
                                                                                                    	at com.example.bankapp.MainActivity.onCreate$lambda$1$lambda$0(MainActivity.kt:45)
                                                                                                    	at com.example.bankapp.MainActivity$$ExternalSyntheticLambda1.invoke(D8$$SyntheticClass:0)
                                                                                                    	at androidx.compose.runtime.internal.ComposableLambdaImpl.invoke(ComposableLambda.kt:122)
                                                                                                    	at androidx.compose.runtime.internal.ComposableLambdaImpl.invoke(ComposableLambda.kt:52)
                                                                                                    	at androidx.compose.runtime.CompositionLocalKt.CompositionLocalProvider(CompositionLocal.kt:398)
                                                                                                    	at androidx.compose.material3.TextKt.ProvideTextStyle(Text.kt:462)
                                                                                                    	at androidx.compose.material3.MaterialThemeKt$MaterialTheme$2.invoke(MaterialTheme.kt:107)
                                                                                                    	at androidx.compose.material3.MaterialThemeKt$MaterialTheme$2.invoke(MaterialTheme.kt:106)
                                                                                                    	at androidx.compose.runtime.internal.ComposableLambdaImpl.invoke(ComposableLambda.kt:122)
                                                                                                    	at androidx.compose.runtime.internal.ComposableLambdaImpl.invoke(ComposableLambda.kt:52)
                                                                                                    	at androidx.compose.runtime.CompositionLocalKt.CompositionLocalProvider(CompositionLocal.kt:378)
                                                                                                    	at androidx.compose.material3.MaterialThemeKt.MaterialTheme(MaterialTheme.kt:99)
                                                                                                    	at androidx.compose.material3.MaterialThemeKt.MaterialTheme(MaterialTheme.kt:60)
                                                                                                    	at com.example.bankapp.ui.theme.ThemeKt.BankAppTheme(Theme.kt:121)
                                                                                                    	at com.example.bankapp.MainActivity.onCreate$lamb

What is the best pattern to fix this navigation restoration bug?
 */