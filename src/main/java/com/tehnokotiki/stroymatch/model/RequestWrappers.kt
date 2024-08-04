package com.tehnokotiki.stroymatch.model

data class RankWorkersRequest(
    val employer: Employer,
    val workers: List<Worker>
)

data class RankEmployersRequest(
    val worker: Worker,
    val employers: List<Employer>
)
