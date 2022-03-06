package dev.yjyoon.coverist.ui.showcover

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yjyoon.coverist.repository.CoverRepository
import javax.inject.Inject

@HiltViewModel
class ShowCoverViewModel @Inject constructor(
    private val coverRepository: CoverRepository
) : ViewModel() {
}