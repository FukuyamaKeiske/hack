package com.tehnokotiki.stroymatch.model

data class User(val username: String, val password: String, val account_type: String)
data class WorkerInformation(val name: String, val birth_date: String, val city: String)
data class EmployerInformation(val company_name: String, val industry: String, val company_size: String)
data class WorkerSearchCriteria(
    val work_experience: String,
    val material_knowledge: String,
    val blueprint_reading: String,
    val team_management: String,
    val tool_experience: String,
    val safety_knowledge: String,
    val communication: String,
    val project_experience: String,
    val planning: String,
    val mechanics_knowledge: String,
    val material_experience: String,
    val construction_experience: String,
    val hvac_experience: String,
    val electrical_experience: String,
    val budget_management: String,
    val foundation_experience: String,
    val problem_solving: String,
    val roofing_experience: String,
    val project_management: String,
    val glazing_experience: String,
    val insulation_experience: String,
    val finishing_experience: String,
    val subcontractor_management: String,
    val exterior_finishing: String,
    val machinery_experience: String
)
data class EmployerSearchCriteria(
    val work_experience: String,
    val material_knowledge: String,
    val blueprint_reading: String,
    val team_management: String,
    val tool_experience: String,
    val safety_knowledge: String,
    val communication: String,
    val project_experience: String,
    val planning: String,
    val mechanics_knowledge: String,
    val material_experience: String,
    val construction_experience: String,
    val hvac_experience: String,
    val electrical_experience: String,
    val budget_management: String,
    val foundation_experience: String,
    val problem_solving: String,
    val roofing_experience: String,
    val project_management: String,
    val glazing_experience: String,
    val insulation_experience: String,
    val finishing_experience: String,
    val subcontractor_management: String,
    val exterior_finishing: String,
    val machinery_experience: String
)
data class WorkerReview(val company_name: String, val rating: Int, val review_text: String)
data class EmployerReview(val company_name: String, val rating: Int, val review_text: String)
data class Worker(
    val username: String,
    val password: String,
    val account_type: String,
    val information: WorkerInformation,
    val search_criteria: WorkerSearchCriteria,
    val reviews: List<WorkerReview>
)
data class Employer(
    val username: String,
    val password: String,
    val account_type: String,
    val information: EmployerInformation,
    val search_criteria: EmployerSearchCriteria,
    val reviews: List<EmployerReview>
)
data class Match(val criterion: String, val score: Float, val percentage: Float)
data class MatchInfo(val score: Float, val matches: List<Match>)
data class RankedWorker(val worker: Worker, val match_info: MatchInfo)
data class RankedEmployer(val employer: Employer, val match_info: MatchInfo)
