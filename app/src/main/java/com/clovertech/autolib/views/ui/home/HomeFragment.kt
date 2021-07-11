package com.clovertech.autolib.views.ui.home


import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.clovertech.autolib.R
import com.clovertech.autolib.model.AgentToken
import com.clovertech.autolib.adapters.ListTasksAdapter
import com.clovertech.autolib.adapters.TaskStepsAdapter
import com.clovertech.autolib.databinding.FragmentHomeBinding
import com.clovertech.autolib.model.Vehicle
import com.clovertech.autolib.viewmodel.NotificationViewModel
import com.clovertech.autolib.viewmodel.TaskViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.math.abs


class HomeFragment : Fragment() {

    private val TAG = "LOG TAG"
    private val taskViewModel: TaskViewModel by activityViewModels()
    private val notificationViewModel: NotificationViewModel by activityViewModels()
    private lateinit var adapterSteps: TaskStepsAdapter
    private lateinit var binding: FragmentHomeBinding
    private lateinit var prefs: SharedPreferences


    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding
            .inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences("AUTOLIB_MAINTAIN", AppCompatActivity.MODE_PRIVATE)
        val adapter = ListTasksAdapter(findNavController())
        val tasksPager = binding.pagerTasksHome
        val taskStepRecycler = binding.taskStepsRecycler

        tasksPager.adapter = adapter
        tasksPager.clipToPadding = false
        tasksPager.clipChildren = false
        tasksPager.offscreenPageLimit = 4

        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(32))

        /*transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }*/

        tasksPager.setPageTransformer(transformer)
        tasksPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(adapter.data.isNotEmpty()){
                    taskViewModel.task = adapter.data[position]
                    adapterSteps.setListSteps(adapter.data[position].steps!!)
                }
            }
        })

        adapterSteps = TaskStepsAdapter(taskViewModel)
        taskStepRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        taskStepRecycler.isNestedScrollingEnabled = false
        taskStepRecycler.adapter = adapterSteps

        val id = prefs.getInt("AGENT_ID",0)
        if (id != 0) {
            taskViewModel.getTasksByIdAgent(requireContext(), id)
            taskViewModel.getAllTasks(requireContext())?.observe(viewLifecycleOwner,{
                val listFiltered = it.filter { task -> ((task.idTaskState == 1) || (task.idTaskState == 2)) }
                adapter.setTaskList(listFiltered)
                adapter.notifyDataSetChanged()
                binding.tasksNumber.text = listFiltered.size.toString()
                tasksPager.requestTransform()
            })
        }

        taskViewModel.getTasksByIdAgent(requireContext(), id)
    }

    override fun onResume() {
        super.onResume()
        sendFCMToken()
    }

    private fun sendFCMToken() {
        val idAgent = prefs.getInt("AGENT_ID",0)
        Log.d(TAG, "FCM token : $idAgent")
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            var token = task.result
            if (token == null) {
                token = ""
            }

            // Post token
            if(idAgent != 0){
                notificationViewModel.postFCMToken(requireContext(), AgentToken(idAgent, token))
            }

            // Log
            Log.d(TAG, "FCM token : $token")

        })
    }

}







