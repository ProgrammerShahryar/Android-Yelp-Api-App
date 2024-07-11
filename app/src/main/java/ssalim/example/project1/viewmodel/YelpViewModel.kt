package ssalim.example.project1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class YelpViewModel(private val repository: YelpRepository) : ViewModel() {

    val businessesLiveData = MutableLiveData<List<YelpData>>()

    fun businesses(location: String) {
        viewModelScope.launch {
            repository.getBuisness(location, businessesLiveData)
        }
    }
}