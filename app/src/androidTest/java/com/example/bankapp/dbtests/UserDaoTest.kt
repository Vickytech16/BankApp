package com.example.bankapp.dbtests


import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bankapp.daos.UserDao
import com.example.bankapp.core.BankDatabase
import com.example.bankapp.entities.dbtables.User
import com.example.bankapp.viewmodels.RegisterViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.test.runTest


@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: BankDatabase
    private lateinit var userDao: UserDao

    private lateinit var registerViewModel: RegisterViewModel

    @Composable
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context,
            BankDatabase::class.java
        )
            .allowMainThreadQueries() // only for tests
            .build()

        userDao = db.userDao()
        registerViewModel = hiltViewModel()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
     fun InsertAndFetchUserByEmail() = runTest {
        val user = User(
            email = "test@maiil.com",
            phoneNumber = "9999999999",
            passwordHashed = "hash123",
            userName = "Bro"
        )

        userDao.createUser(user)

        val user2 = User(
            email = "test@mail.com",
            phoneNumber = "999999879999",
            passwordHashed = "hash123",
            userName = "Dicky"
        )

       userDao.createUser(user2)

        val result = userDao.getUserByEmail("test@mail.com")

        assertNotNull(result)
        assertEquals("test@mail.com", result?.email)
        Log.d("TEST", "User fetched successfully")
        Log.d("TEST", "User email is matched ${user.email} ${user.phoneNumber} ${user.userId}")
        Log.d("TEST", "User email is matched ${user2.email} ${user2.phoneNumber} ${user2.userId}")
        Log.d("TEST", "FROM DB: ${userDao.getUserByEmail("testmaiil")?.userId} " +
                " ${userDao.getUserByEmail("test@mail.com")?.userId}  ")

        for(user in userDao.getAllUsers())
        {
            Log.d("TEST", "${user.email} ${user.phoneNumber} ${user.userId} ${user.userName}")
        }
    }

    @Test
    fun viewModelTest() = runTest {
        val user = User(
            email = "test@maiil.com",
            phoneNumber = "9999999999",
            passwordHashed = "hash123",
            userName = "Bro"
        )

        registerViewModel.onUserNameChange(user.userName)
        registerViewModel.onEmailChange(user.email)
        registerViewModel.onPasswordChange(user.passwordHashed)
        registerViewModel.onPhoneNumberChange(user.phoneNumber)

        registerViewModel.onSubmit()

        for(user in userDao.getAllUsers())
        {
            Log.d("TEST", "${user.email} ${user.phoneNumber} ${user.userId} ${user.userName}")
        }


    }



}


