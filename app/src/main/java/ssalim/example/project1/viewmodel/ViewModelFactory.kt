package ssalim.example.project1


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class YelpViewModelFactory(private val repository: YelpRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(YelpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return YelpViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}