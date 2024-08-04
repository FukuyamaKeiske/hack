package com.tehnokotiki.stroymatch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tehnokotiki.stroymatch.adapter.CardAdapter
import com.tehnokotiki.stroymatch.model.*
import com.tehnokotiki.stroymatch.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cardAdapter: CardAdapter
    private lateinit var searchButton: Button
    private lateinit var apiService: ApiService
    private val PREFS_NAME = "job_matching_prefs"
    private val CRITERIA_KEY = "search_criteria"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view)
        searchButton = view.findViewById(R.id.btn_search)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        apiService = Retrofit.Builder()
            .baseUrl("http://192.168.1.78:8000") // Убедитесь, что URL правильный
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        searchButton.setOnClickListener {
            val selectedRole = (activity as MainActivity).getSelectedRole()
            val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

            if (selectedRole == "Employer") {
                val employer = Employer(
                    username = sharedPreferences.getString("phone", "") ?: "",
                    password = sharedPreferences.getString("password", "") ?: "",
                    account_type = "employer",
                    information = EmployerInformation(
                        company_name = sharedPreferences.getString("employer_company_name", "") ?: "",
                        industry = sharedPreferences.getString("employer_industry", "") ?: "",
                        company_size = sharedPreferences.getString("employer_company_size", "") ?: ""
                    ),
                    search_criteria = EmployerSearchCriteria(
                        work_experience = sharedPreferences.getString("$CRITERIA_KEY" + "work_experience", "") ?: "",
                        material_knowledge = sharedPreferences.getString("$CRITERIA_KEY" + "material_knowledge", "") ?: "",
                        blueprint_reading = sharedPreferences.getString("$CRITERIA_KEY" + "blueprint_reading", "") ?: "",
                        team_management = sharedPreferences.getString("$CRITERIA_KEY" + "team_management", "") ?: "",
                        tool_experience = sharedPreferences.getString("$CRITERIA_KEY" + "tool_experience", "") ?: "",
                        safety_knowledge = sharedPreferences.getString("$CRITERIA_KEY" + "safety_knowledge", "") ?: "",
                        communication = sharedPreferences.getString("$CRITERIA_KEY" + "communication", "") ?: "",
                        project_experience = sharedPreferences.getString("$CRITERIA_KEY" + "project_experience", "") ?: "",
                        planning = sharedPreferences.getString("$CRITERIA_KEY" + "planning", "") ?: "",
                        mechanics_knowledge = sharedPreferences.getString("$CRITERIA_KEY" + "mechanics_knowledge", "") ?: "",
                        material_experience = sharedPreferences.getString("$CRITERIA_KEY" + "material_experience", "") ?: "",
                        construction_experience = sharedPreferences.getString("$CRITERIA_KEY" + "construction_experience", "") ?: "",
                        hvac_experience = sharedPreferences.getString("$CRITERIA_KEY" + "hvac_experience", "") ?: "",
                        electrical_experience = sharedPreferences.getString("$CRITERIA_KEY" + "electrical_experience", "") ?: "",
                        budget_management = sharedPreferences.getString("$CRITERIA_KEY" + "budget_management", "") ?: "",
                        foundation_experience = sharedPreferences.getString("$CRITERIA_KEY" + "foundation_experience", "") ?: "",
                        problem_solving = sharedPreferences.getString("$CRITERIA_KEY" + "problem_solving", "") ?: "",
                        roofing_experience = sharedPreferences.getString("$CRITERIA_KEY" + "roofing_experience", "") ?: "",
                        project_management = sharedPreferences.getString("$CRITERIA_KEY" + "project_management", "") ?: "",
                        glazing_experience = sharedPreferences.getString("$CRITERIA_KEY" + "glazing_experience", "") ?: "",
                        insulation_experience = sharedPreferences.getString("$CRITERIA_KEY" + "insulation_experience", "") ?: "",
                        finishing_experience = sharedPreferences.getString("$CRITERIA_KEY" + "finishing_experience", "") ?: "",
                        subcontractor_management = sharedPreferences.getString("$CRITERIA_KEY" + "subcontractor_management", "") ?: "",
                        exterior_finishing = sharedPreferences.getString("$CRITERIA_KEY" + "exterior_finishing", "") ?: "",
                        machinery_experience = sharedPreferences.getString("$CRITERIA_KEY" + "machinery_experience", "") ?: ""
                    ),
                    reviews = listOf()
                )
                searchWorkers(employer)
            } else if (selectedRole == "Worker") {
                val worker = Worker(
                    username = sharedPreferences.getString("phone", "") ?: "",
                    password = sharedPreferences.getString("password", "") ?: "",
                    account_type = "worker",
                    information = WorkerInformation(
                        name = sharedPreferences.getString("worker_name", "") ?: "",
                        birth_date = sharedPreferences.getString("worker_birth_date", "") ?: "",
                        city = sharedPreferences.getString("worker_city", "") ?: ""
                    ),
                    search_criteria = WorkerSearchCriteria(
                        work_experience = sharedPreferences.getString("$CRITERIA_KEY" + "work_experience", "") ?: "",
                        material_knowledge = sharedPreferences.getString("$CRITERIA_KEY" + "material_knowledge", "") ?: "",
                        blueprint_reading = sharedPreferences.getString("$CRITERIA_KEY" + "blueprint_reading", "") ?: "",
                        team_management = sharedPreferences.getString("$CRITERIA_KEY" + "team_management", "") ?: "",
                        tool_experience = sharedPreferences.getString("$CRITERIA_KEY" + "tool_experience", "") ?: "",
                        safety_knowledge = sharedPreferences.getString("$CRITERIA_KEY" + "safety_knowledge", "") ?: "",
                        communication = sharedPreferences.getString("$CRITERIA_KEY" + "communication", "") ?: "",
                        project_experience = sharedPreferences.getString("$CRITERIA_KEY" + "project_experience", "") ?: "",
                        planning = sharedPreferences.getString("$CRITERIA_KEY" + "planning", "") ?: "",
                        mechanics_knowledge = sharedPreferences.getString("$CRITERIA_KEY" + "mechanics_knowledge", "") ?: "",
                        material_experience = sharedPreferences.getString("$CRITERIA_KEY" + "material_experience", "") ?: "",
                        construction_experience = sharedPreferences.getString("$CRITERIA_KEY" + "construction_experience", "") ?: "",
                        hvac_experience = sharedPreferences.getString("$CRITERIA_KEY" + "hvac_experience", "") ?: "",
                        electrical_experience = sharedPreferences.getString("$CRITERIA_KEY" + "electrical_experience", "") ?: "",
                        budget_management = sharedPreferences.getString("$CRITERIA_KEY" + "budget_management", "") ?: "",
                        foundation_experience = sharedPreferences.getString("$CRITERIA_KEY" + "foundation_experience", "") ?: "",
                        problem_solving = sharedPreferences.getString("$CRITERIA_KEY" + "problem_solving", "") ?: "",
                        roofing_experience = sharedPreferences.getString("$CRITERIA_KEY" + "roofing_experience", "") ?: "",
                        project_management = sharedPreferences.getString("$CRITERIA_KEY" + "project_management", "") ?: "",
                        glazing_experience = sharedPreferences.getString("$CRITERIA_KEY" + "glazing_experience", "") ?: "",
                        insulation_experience = sharedPreferences.getString("$CRITERIA_KEY" + "insulation_experience", "") ?: "",
                        finishing_experience = sharedPreferences.getString("$CRITERIA_KEY" + "finishing_experience", "") ?: "",
                        subcontractor_management = sharedPreferences.getString("$CRITERIA_KEY" + "subcontractor_management", "") ?: "",
                        exterior_finishing = sharedPreferences.getString("$CRITERIA_KEY" + "exterior_finishing", "") ?: "",
                        machinery_experience = sharedPreferences.getString("$CRITERIA_KEY" + "machinery_experience", "") ?: ""
                    ),
                    reviews = listOf()
                )
                searchEmployers(worker)
            }
        }
    }

    private fun searchWorkers(employer: Employer) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getWorkers()
            if (response.isSuccessful) {
                val workers = response.body() ?: listOf()
                val request = RankWorkersRequest(employer, workers)
                val rankResponse = apiService.rankWorkers(request)
                if (rankResponse.isSuccessful) {
                    val rankedWorkers = rankResponse.body()
                    val sortedWorkers = rankedWorkers?.sortedByDescending { it.match_info.score }
                    withContext(Dispatchers.Main) {
                        cardAdapter = CardAdapter(workers = sortedWorkers ?: listOf())
                        recyclerView.adapter = cardAdapter
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Ошибка ранжирования работников", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка получения данных работников", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun searchEmployers(worker: Worker) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getEmployers()
            if (response.isSuccessful) {
                val employers = response.body() ?: listOf()
                val request = RankEmployersRequest(worker, employers)
                val rankResponse = apiService.rankEmployers(request)
                if (rankResponse.isSuccessful) {
                    val rankedEmployers = rankResponse.body()
                    val sortedEmployers = rankedEmployers?.sortedByDescending { it.match_info.score }
                    withContext(Dispatchers.Main) {
                        cardAdapter = CardAdapter(employers = sortedEmployers ?: listOf())
                        recyclerView.adapter = cardAdapter
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Ошибка ранжирования работодателей", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка получения данных работодателей", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
