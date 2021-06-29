package com.clovertech.autolib.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clovertech.autolib.model.Tache
import com.clovertech.autolib.model.TacheModel
import com.clovertech.autolib.repository.TacheRepo
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*

class TacheViewModel : ViewModel() {

    var task: Tache =
        Tache(
            0, 0, "", "", 0, Date(), Date(),
            mutableListOf(), mutableListOf(), ""
        )




    fun updateTache(context: Context, tache: Tache) {
        TacheRepo.updateTache(context, tache)
    }


    fun getAllTaches(context: Context): LiveData<List<Tache>>? {
        return TacheRepo.getAllTaches(context)
    }

    fun getTacheIdAgent(context: Context, id: Int) {
        viewModelScope.launch {
            TacheRepo.getTacheIdAgent(context, id)
        }
    }



}