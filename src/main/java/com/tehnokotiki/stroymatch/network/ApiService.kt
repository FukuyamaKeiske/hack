package com.tehnokotiki.stroymatch.network

import com.tehnokotiki.stroymatch.model.*
import com.tehnokotiki.stroymatch.model.RankEmployersRequest
import com.tehnokotiki.stroymatch.model.RankWorkersRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class User(val username: String, val password: String, val account_type: String)
data class WorkerInformation(val name: String, val birth_date: String, val city: String)
data class EmployerInformation(val company_name: String, val industry: String, val company_size: String)
data class RankWorkersRequest(val employer: Employer, val workers: List<Worker>)
data class RankEmployersRequest(val worker: Worker, val employers: List<Employer>)

interface ApiService {
    @POST("/register/")
    suspend fun registerWorker(@Body user: User): Response<Worker>

    @POST("/register/")
    suspend fun registerEmployer(@Body user: User): Response<Employer>

    @POST("/login/")
    suspend fun loginWorker(@Body user: User): Response<Worker>

    @POST("/login/")
    suspend fun loginEmployer(@Body user: User): Response<Employer>

    @POST("/update_worker_info/")
    suspend fun updateWorkerInfo(@Body username: String, @Body info: WorkerInformation): Response<Worker>

    @POST("/update_employer_info/")
    suspend fun updateEmployerInfo(@Body username: String, @Body info: EmployerInformation): Response<Employer>

    @POST("/rank_workers/")
    suspend fun rankWorkers(@Body request: RankWorkersRequest): Response<List<RankedWorker>>

    @POST("/rank_employers/")
    suspend fun rankEmployers(@Body request: RankEmployersRequest): Response<List<RankedEmployer>>

    @GET("/workers/")
    suspend fun getWorkers(): Response<List<Worker>>

    @GET("/employers/")
    suspend fun getEmployers(): Response<List<Employer>>
}
