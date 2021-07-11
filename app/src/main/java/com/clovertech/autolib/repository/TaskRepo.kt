package com.clovertech.autolib.repository

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import com.clovertech.autolib.R
import com.clovertech.autolib.cache.db.AutolibDatabase
import com.clovertech.autolib.model.Task
import com.clovertech.autolib.model.TaskState
import com.clovertech.autolib.network.client.TaskApiClient
import com.clovertech.autolib.utils.DialogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

object TaskRepo {

        var appDb: AutolibDatabase? = null
        var taches: LiveData<List<Task>>? = null

        private fun initializeDB(context: Context): AutolibDatabase {
            return AutolibDatabase.getDatabaseClient(context)
        }


        /**
         * Inserer une tache dans la cache
         * @param task
         * @param context*/
        private fun insertTask(context: Context, task: Task) {

            appDb = initializeDB(context)

            CoroutineScope(Dispatchers.IO).launch {
                appDb!!.taskDao().addTask(task)
            }

        }

        /**
         * Recuperer la liste des taches existantes dans la cache
         * @return list<Tache>
         *  */
        fun getAllTasks(context: Context): LiveData<List<Task>>? {

            appDb = initializeDB(context)

            taches = appDb!!.taskDao().getAllTasks()

            return taches
        }


        /**
         * Recuperer la liste des taches d'un agent par son id du service et les insere une par une dans la cache
         * @param id
         * */
        suspend fun getTaskByIdAgent(context: Context, id: Int) {
            val response = TaskApiClient.TASK_API_SERVICE.getTasksById(id)

            if (response.isSuccessful) {
                val listTask = response.body()!!
                for (task in listTask) {
                    insertTask(context, task)
                }
            }
        }

        /**
         * Mettre à jour l'etat des etapes d'une tache dans la cache
         * @param context
         * @param task*/
        fun updateTask(context: Context, task: Task) {

            appDb = initializeDB(context)

            val listFiltered = task.steps?.filter { it.completed }
            if (listFiltered?.size == task.steps?.size) {
                DialogUtils.with(context)
                    .showDialog(context.getString(R.string.closeDialogTitle),
                        context.getString(R.string.closeMessagePre) + task.taskTitle,
                        { _: DialogInterface, _: Int ->
                            CoroutineScope(Dispatchers.IO).launch {

                            val response = TaskApiClient.TASK_API_SERVICE.updateTaskState(
                                    task.uuid, TaskState(3))
                            task.idTaskState = 3
                            task.endDate = Calendar.getInstance().time
                            appDb!!.taskDao().updateTask(task)
                        }
                    }, null
                )
            }
            else {
                if (listFiltered?.size == 1)
                    CoroutineScope(Dispatchers.IO).launch {
                        val response =
                            TaskApiClient.TASK_API_SERVICE.updateTaskState(
                                task.uuid,
                                TaskState(2)
                            )
                        task.idTaskState = 2
                        appDb!!.taskDao().updateTask(task)
                    }
                else {
                    CoroutineScope(Dispatchers.IO).launch {
                        appDb!!.taskDao().updateTask(task)
                    }
                }
            }
        }
}

