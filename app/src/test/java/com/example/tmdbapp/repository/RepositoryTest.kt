package com.example.tmdbapp.repository

import com.example.tmdbapp.utils.Resource
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RepositoryTest {
    private lateinit var repository: Repository
    private val mockContext = mockk<android.content.Context>(relaxed = true)

    @Before
    fun setup() {
        repository = Repository(mockContext)
    }

    @Test
    fun `safeApiCall returns Success when API call succeeds`() = runTest {
        // Given
        val expectedResult = "Test Success"
        val apiCall: suspend () -> String = { expectedResult }

        // When
        val result = repository.safeApiCall(apiCall)

        // Then
        assertTrue(result is Resource.Success<String>)
        assertEquals(expectedResult, (result as Resource.Success<String>).data)
    }

    @Test
    fun `safeApiCall returns Error when API call throws exception`() = runTest {
        // Given
        val errorMessage = "Test Error"
        val apiCall: suspend () -> String = { throw Exception(errorMessage) }

        // When
        val result = repository.safeApiCall(apiCall)

        // Then
        assertTrue(result is Resource.Error<String>)
        assertEquals(errorMessage, (result as Resource.Error<String>).message)
    }

    @Test
    fun `safeApiCall returns Error with default message when exception has no message`() = runTest {
        // Given
        val apiCall: suspend () -> String = { throw Exception() }

        // When
        val result = repository.safeApiCall(apiCall)

        // Then
        assertTrue(result is Resource.Error<String>)
        assertEquals("An unexpected error occurred", (result as Resource.Error<String>).message)
    }

    @Test
    fun `safeApiCall works with different types`() = runTest {
        // Given
        val expectedResult = 42
        val apiCall: suspend () -> Int = { expectedResult }

        // When
        val result = repository.safeApiCall(apiCall)

        // Then
        assertTrue(result is Resource.Success<Int>)
        assertEquals(expectedResult, (result as Resource.Success<Int>).data)
    }

    @Test
    fun `safeApiCall works with custom data class`() = runTest {
        // Given
        data class TestData(val id: Int, val name: String)
        val expectedResult = TestData(1, "Test")
        val apiCall: suspend () -> TestData = { expectedResult }

        // When
        val result = repository.safeApiCall(apiCall)

        // Then
        assertTrue(result is Resource.Success<TestData>)
        assertEquals(expectedResult, (result as Resource.Success<TestData>).data)
    }
} 