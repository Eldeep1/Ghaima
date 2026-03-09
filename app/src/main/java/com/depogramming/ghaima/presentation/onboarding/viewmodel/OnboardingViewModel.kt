package com.depogramming.ghaima.presentation.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.depogramming.ghaima.data.onBoarding.LanguageModel
import com.depogramming.ghaima.data.usersettings.UserSettingsRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    val userSettingsRepo: UserSettingsRepo
): ViewModel() {
    private val _languages= MutableStateFlow<List<LanguageModel>>(listOf())
    val language: StateFlow<List<LanguageModel>> =_languages.asStateFlow()

    init {
        getLanguages()
    }
    lateinit var selectedLanguage: LanguageModel
     fun getLanguages(){
         //actually, here we should get the stored language if any exists
         //but if any exists, how will the user enters that screen?
        viewModelScope.launch {
            _languages.value=userSettingsRepo.getLanguages()
            selectedLanguage=language.value[0]
        }
    }
    fun selectLanguage(languageModel: LanguageModel){
        //TODO call the room or shared shit and store the selection there
        selectedLanguage=languageModel
    }
}

@Suppress("UNCHECKED_CAST")
class OnboardingViewModelFactory( private val repository: UserSettingsRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OnboardingViewModel(repository) as T
    }
}