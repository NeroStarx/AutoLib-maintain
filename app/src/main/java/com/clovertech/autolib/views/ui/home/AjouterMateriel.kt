package com.clovertech.autolib.views.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.clovertech.autolib.R
import com.clovertech.autolib.model.Materiel
import com.clovertech.autolib.model.NewEquipment
import com.clovertech.autolib.utils.PrefUtils
import com.clovertech.autolib.viewmodel.EquipmentViewModel
import com.clovertech.autolib.viewmodel.TacheViewModel
import kotlinx.android.synthetic.main.fragment_ajouter_materiel.*


class AjouterMateriel : Fragment(), AdapterView.OnItemSelectedListener {
    lateinit var equipmentViewModel: EquipmentViewModel
    var equipements = mutableListOf("huile", "gaz", "peinture")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ajouter_materiel, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        equipmentViewModel =
            ViewModelProvider(requireActivity()).get(EquipmentViewModel::class.java)
        var viewModelTask = ViewModelProvider(requireActivity()).get(TacheViewModel::class.java)
        equipmentViewModel.getAllEquipment()
        equipmentViewModel.responseEquipment.observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful){
                /*equipements= mutableListOf()
                for (modelEquipment in it.body()!!){
                    equipements.add(modelEquipment.equipmentName)
                }*/
            }
        })


        //Appliquer OnItemSelectedListener sur l'objet spinner
        materielNom.onItemSelectedListener = this
        //Creation d'un ArrayAdapter avec la liste des equipements
        val spinnerAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(requireActivity(), android.R.layout.simple_spinner_item,
                equipements as List<Any?>
            )
        //Relier adapter au spinner
        materielNom.adapter = spinnerAdapter



        ajouter_materiel.setOnClickListener {
            val view=it
            val token = PrefUtils.with(requireContext()).getString(PrefUtils.Keys.taskUuid, "")
            var newEquipement = token?.let { it1 ->
                NewEquipment(
                    materielDescript.text.toString(),
                    materielQuantite.text.toString().toInt(),
                    "282d4458-aaeb-4e92-a674-12320b1de46a",
                    viewModelTask.task.uuid
                )

            }

            //Ajout local
            var material =
                Materiel(materielDescript.text.toString(), materielQuantite.text.toString())

            if (newEquipement != null) {
                equipmentViewModel.addMateriel(newEquipement)

                equipmentViewModel.response.observe(viewLifecycleOwner, Observer {
                    if (it.code() == 200) {
                        var listEquipment: MutableList<Materiel>? =
                            viewModelTask.task.usedEquipements as MutableList<Materiel>?
                        if (listEquipment != null) {
                            listEquipment.add(material)
                        }
                        viewModelTask.task.usedEquipements = listEquipment
                        viewModelTask.updateTache(requireContext(), viewModelTask.task)

                        Toast.makeText(requireContext(), it.code().toString(), Toast.LENGTH_SHORT)
                            .show()
                        view.findNavController().navigate(R.id.action_ajouterMateriel_to_detailTache)
                    } else {
                        Toast.makeText(requireContext(), it.code().toString(), Toast.LENGTH_SHORT)
                            .show()
                    }

                })
            }
        }


    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Toast.makeText(context, equipements[pos], Toast.LENGTH_LONG).show();

    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }


}

