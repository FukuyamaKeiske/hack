package com.tehnokotiki.stroymatch

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private lateinit var personalInfoContainer: LinearLayout
    private lateinit var searchCriteriaContainer: LinearLayout
    private lateinit var personalInfoTitle: TextView
    private val PREFS_NAME = "job_matching_prefs"
    private val CRITERIA_KEY = "search_criteria"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        personalInfoContainer = view.findViewById(R.id.personal_info_container)
        searchCriteriaContainer = view.findViewById(R.id.search_criteria_container)
        personalInfoTitle = view.findViewById(R.id.tv_personal_info)

        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val role = sharedPreferences.getString("selected_role", null)

        if (role == "worker") {
            personalInfoTitle.text = "Личная информация"
            addPersonalInfoCard(sharedPreferences, "worker")
        } else if (role == "employer") {
            personalInfoTitle.text = "Информация о компании"
            addPersonalInfoCard(sharedPreferences, "employer")
        }

        updateCriteria(role, sharedPreferences)
    }

    private fun updateCriteria(role: String?, sharedPreferences: SharedPreferences) {
        searchCriteriaContainer.removeAllViews()

        val criteria = listOf(
            getString(R.string.work_experience),
            getString(R.string.material_knowledge),
            getString(R.string.blueprint_reading),
            getString(R.string.team_management),
            getString(R.string.tool_experience),
            getString(R.string.safety_knowledge),
            getString(R.string.communication),
            getString(R.string.project_experience),
            getString(R.string.planning),
            getString(R.string.mechanics_knowledge),
            getString(R.string.material_experience),
            getString(R.string.construction_experience),
            getString(R.string.hvac_experience),
            getString(R.string.electrical_experience),
            getString(R.string.budget_management),
            getString(R.string.foundation_experience),
            getString(R.string.problem_solving),
            getString(R.string.roofing_experience),
            getString(R.string.project_management),
            getString(R.string.glazing_experience),
            getString(R.string.insulation_experience),
            getString(R.string.finishing_experience),
            getString(R.string.subcontractor_management),
            getString(R.string.exterior_finishing),
            getString(R.string.machinery_experience)
        )

        criteria.forEach { criterion ->
            addCriteriaCard(criterion, sharedPreferences, arrayOf("Low", "Medium", "High"))
        }
    }

    private fun addPersonalInfoCard(sharedPreferences: SharedPreferences, role: String) {
        val cardView = CardView(requireContext())
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(16, 16, 16, 16)
        cardView.layoutParams = layoutParams
        cardView.radius = 16f
        cardView.setCardBackgroundColor(resources.getColor(android.R.color.white))
        cardView.cardElevation = 8f

        val cardContent = LinearLayout(requireContext())
        cardContent.orientation = LinearLayout.VERTICAL
        cardContent.setPadding(16, 16, 16, 16)

        if (role == "worker") {
            val nameLabel = TextView(requireContext())
            nameLabel.text = "Имя"
            nameLabel.textSize = 18f
            cardContent.addView(nameLabel)

            val nameEditText = EditText(requireContext())
            nameEditText.setText(sharedPreferences.getString("worker_name", ""))
            cardContent.addView(nameEditText)

            val birthDateLabel = TextView(requireContext())
            birthDateLabel.text = "Дата рождения"
            birthDateLabel.textSize = 18f
            cardContent.addView(birthDateLabel)

            val birthDateEditText = EditText(requireContext())
            birthDateEditText.setText(sharedPreferences.getString("worker_birth_date", ""))
            cardContent.addView(birthDateEditText)

            val cityLabel = TextView(requireContext())
            cityLabel.text = "Город проживания"
            cityLabel.textSize = 18f
            cardContent.addView(cityLabel)

            val cityEditText = EditText(requireContext())
            cityEditText.setText(sharedPreferences.getString("worker_city", ""))
            cardContent.addView(cityEditText)

            nameEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    sharedPreferences.edit().putString("worker_name", nameEditText.text.toString()).apply()
                }
            }

            birthDateEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    sharedPreferences.edit().putString("worker_birth_date", birthDateEditText.text.toString()).apply()
                }
            }

            cityEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    sharedPreferences.edit().putString("worker_city", cityEditText.text.toString()).apply()
                }
            }
        } else if (role == "employer") {
            val companyNameLabel = TextView(requireContext())
            companyNameLabel.text = "Название компании"
            companyNameLabel.textSize = 18f
            cardContent.addView(companyNameLabel)

            val companyNameEditText = EditText(requireContext())
            companyNameEditText.setText(sharedPreferences.getString("employer_company_name", ""))
            cardContent.addView(companyNameEditText)

            val industryLabel = TextView(requireContext())
            industryLabel.text = "Род деятельности"
            industryLabel.textSize = 18f
            cardContent.addView(industryLabel)

            val industryEditText = EditText(requireContext())
            industryEditText.setText(sharedPreferences.getString("employer_industry", ""))
            cardContent.addView(industryEditText)

            val companySizeLabel = TextView(requireContext())
            companySizeLabel.text = "Размер компании"
            companySizeLabel.textSize = 18f
            cardContent.addView(companySizeLabel)

            val companySizeEditText = EditText(requireContext())
            companySizeEditText.setText(sharedPreferences.getString("employer_company_size", ""))
            cardContent.addView(companySizeEditText)

            companyNameEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    sharedPreferences.edit().putString("employer_company_name", companyNameEditText.text.toString()).apply()
                }
            }

            industryEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    sharedPreferences.edit().putString("employer_industry", industryEditText.text.toString()).apply()
                }
            }

            companySizeEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    sharedPreferences.edit().putString("employer_company_size", companySizeEditText.text.toString()).apply()
                }
            }
        }

        cardView.addView(cardContent)
        personalInfoContainer.addView(cardView)
    }

    private fun addCriteriaCard(fieldName: String, sharedPreferences: SharedPreferences, options: Array<String>, isPersonalInfo: Boolean = false) {
        val cardView = CardView(requireContext())
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(16, 16, 16, 16)
        cardView.layoutParams = layoutParams
        cardView.radius = 16f
        cardView.setCardBackgroundColor(resources.getColor(android.R.color.white))
        cardView.cardElevation = 8f

        val cardContent = LinearLayout(requireContext())
        cardContent.orientation = LinearLayout.VERTICAL
        cardContent.setPadding(16, 16, 16, 16)

        val fieldLabel = TextView(requireContext())
        fieldLabel.text = fieldName
        fieldLabel.textSize = 18f
        cardContent.addView(fieldLabel)

        val spinner = Spinner(requireContext())
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val savedValue = sharedPreferences.getString("$CRITERIA_KEY$fieldName", "")
        if (savedValue != null) {
            val spinnerPosition = adapter.getPosition(savedValue)
            spinner.setSelection(spinnerPosition)
        }
        cardContent.addView(spinner)

        cardView.addView(cardContent)

        val container = if (isPersonalInfo) personalInfoContainer else searchCriteriaContainer
        container.addView(cardView)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedValue = parent?.getItemAtPosition(position).toString()
                sharedPreferences.edit().putString("$CRITERIA_KEY$fieldName", selectedValue).apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun createSectionTitle(title: String): TextView {
        val titleView = TextView(requireContext())
        titleView.text = title
        titleView.textSize = 20f
        titleView.setTypeface(null, Typeface.BOLD)
        titleView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        titleView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(16, 16, 16, 0)
        }
        return titleView
    }
}

