package com.dicoding.picodiploma.loginwithanimation.view.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.picodiploma.loginwithanimation.DummyData
import com.dicoding.picodiploma.loginwithanimation.api.SignUpResponse
import com.dicoding.picodiploma.loginwithanimation.repository.Repository
import com.dicoding.picodiploma.loginwithanimation.utility.NetworkResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SignUpViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var mockRepo: Repository
    private lateinit var signupViewModel: SignUpViewModel

    @Before
    fun setUp() {
        signupViewModel = SignUpViewModel(mockRepo)
    }

    @Test
    fun `when signup successful and not null`() {
        val expectedUser = MutableLiveData<NetworkResponse<SignUpResponse>>()
        expectedUser.value = NetworkResponse.Success(DummyData.generateDummySignUpResponse())

        Mockito.`when`(mockRepo.signUp(
            name = "Didit Imawan",
            email = "diditimawan345@gmail.com",
            password = "12345678"
        )).thenReturn(expectedUser)
        val actualUser = signupViewModel.signUp(
            name = "Didit Imawan",
            email = "diditimawan345@gmail.com",
            password = "12345678"
        ).value

        Mockito.verify(mockRepo).signUp(
            name = "Didit Imawan",
            email = "diditimawan345@gmail.com",
            password = "12345678"
        )
        Assert.assertTrue(actualUser is NetworkResponse.Success)
        Assert.assertNotNull(actualUser)
    }
}