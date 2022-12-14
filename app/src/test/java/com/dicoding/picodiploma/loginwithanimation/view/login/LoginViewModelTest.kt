package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.picodiploma.loginwithanimation.DummyData
import com.dicoding.picodiploma.loginwithanimation.MainDispatcherRule
import com.dicoding.picodiploma.loginwithanimation.api.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.api.UserResponse
import com.dicoding.picodiploma.loginwithanimation.model.UserPreference
import com.dicoding.picodiploma.loginwithanimation.repository.Repository
import com.dicoding.picodiploma.loginwithanimation.utility.NetworkResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val dispatch = MainDispatcherRule()

    @Mock private val mockStory = Mockito.mock(Repository::class.java)
    @Mock private val mockPref = Mockito.mock(UserPreference::class.java)
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(mockPref, mockStory)
    }

    @Test
    fun `when user login saved successful to preference`() = runTest{
        loginViewModel.saveUser(UserResponse(
            userId = "A07",
            name = "Imawan",
            token = "abcde12345"
        ))
        Mockito.verify(mockPref).saveUser(
            UserResponse(
            userId = "A07",
            name = "Imawan",
            token = "abcde12345"
        )
        )
    }

    @Test
    fun `when user successfully login using email and password`() = runTest{
        val expectedData = MutableLiveData<NetworkResponse<LoginResponse>>()
        expectedData.value = NetworkResponse.Success(DummyData.generateDummyLoginResponse())

        Mockito.`when`(mockStory.loginUser("diditimawan10@gmail.com", "12345678")).thenReturn(expectedData)
        val actualUser = loginViewModel.loginUser("diditimawan10@gmail.com", "12345678").value

        Mockito.verify(mockStory).loginUser("diditimawan10@gmail.com", "12345678")
        assertTrue(actualUser is NetworkResponse.Success)
        assertNotNull(actualUser)
    }
}