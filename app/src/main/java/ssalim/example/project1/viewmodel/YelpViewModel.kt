package ssalim.example.project1.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ssalim.example.project1.YelpData
import ssalim.example.project1.YelpRepository

class YelpViewModel(private val repository: YelpRepository) : ViewModel() {

    val businessesLiveData = MutableLiveData<List<YelpData>>()
    private val _autocompleteSuggestionsLiveData = MutableLiveData<List<String>>()

    fun sBusinesses(location: String) {
        viewModelScope.launch {
            repository.sGetBusiness(location, businessesLiveData)
        }
    }
    fun sGetAutoSuggestions(suggestions: List<String>) {
        _autocompleteSuggestionsLiveData.postValue(suggestions)
    }
}