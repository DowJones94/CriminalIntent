package ru.boxtoyou.android.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()

// TODO:  Запилить импорт и экспорт из файла

//    val onCrimesImported: LiveData<List<Crime>> = MutableLiveData()
//
//    fun onImportClicked() {
//        crimeRepository.getFromFile()
//    }

}