package com.investia.app.util

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Safely execute API calls with proper error handling and Turkish error messages.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(apiCall())
    } catch (e: SocketTimeoutException) {
        Resource.Error("Sunucu yanıt vermedi, tekrar deneyin")
    } catch (e: IOException) {
        Resource.Error("İnternet bağlantınızı kontrol edin")
    } catch (e: HttpException) {
        val errorMsg = when (e.code()) {
            401 -> "Oturum süreniz doldu"
            403 -> "Bu işlem için yetkiniz yok"
            404 -> "İstenen veri bulunamadı"
            429 -> "Çok fazla istek, lütfen bekleyin"
            in 500..599 -> "Sunucu hatası (${e.code()})"
            else -> "Hata: ${e.code()}"
        }
        Resource.Error(errorMsg)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Beklenmeyen bir hata oluştu")
    }
}

/**
 * Extension to convert Flow<T> into Flow<Resource<T>>
 */
fun <T> Flow<T>.asResource(): Flow<Resource<T>> {
    return this
        .map<T, Resource<T>> { Resource.Success(it) }
        .onStart { emit(Resource.Loading()) }
        .catch { emit(Resource.Error(it.message ?: "Hata oluştu")) }
}
