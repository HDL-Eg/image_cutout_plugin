package com.example.image_cutout_plugin.cutout.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.ensureActive

object ImageTaskManager {

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private var currentJob: Job? = null

    fun getTaskScope() = coroutineScope

    fun cancel() {
        job.cancelChildren()
    }

    fun updateCurrentJob(job: Job) {
        currentJob = job
    }

    fun ensureActive() {
        currentJob?.ensureActive()
    }
}