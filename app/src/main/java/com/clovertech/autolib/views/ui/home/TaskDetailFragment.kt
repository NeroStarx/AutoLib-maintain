package com.clovertech.autolib.views.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clovertech.autolib.R
import com.clovertech.autolib.adapters.MaterialAdapter
import com.clovertech.autolib.adapters.TaskStepsAdapter
import com.clovertech.autolib.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.fragment_detail_task.*


class TaskDetailFragment : Fragment() {
    lateinit var adapterSteps: TaskStepsAdapter
    lateinit var adapterMateriels: MaterialAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_task, container, false)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var viewModel = ViewModelProvider(requireActivity()).get(TaskViewModel::class.java)

        titreTask.text = viewModel.task.taskTitle
        descriptTask.text = viewModel.task.description
        idVoiture.text = viewModel.task.idVehicule.toString()
        adapterSteps = TaskStepsAdapter(viewModel)
        task_steps_recycler.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        task_steps_recycler.adapter = adapterSteps
        viewModel.task.steps?.let { adapterSteps.setListSteps(it) }

        adapterMateriels = MaterialAdapter(requireActivity(), viewModel)
        matrialRecyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        matrialRecyclerView.adapter = adapterMateriels

        if (viewModel.task.usedEquipements !=null){
            Toast.makeText(requireContext(), viewModel.task.usedEquipements!!.size.toString(), Toast.LENGTH_SHORT).show()
            if (viewModel.task.usedEquipements!!.size !=0){
                ajouterMaterialText.isVisible=false
            }
            else{
                ajouterMaterialText.isVisible=true
            }
            viewModel.task.usedEquipements?.let { adapterMateriels.setListMaterial(it) }
        }


        addMaterial.setOnClickListener() {
            it.findNavController()?.navigate(R.id.action_detailTache_to_ajouterMateriel)
        }

    }


}