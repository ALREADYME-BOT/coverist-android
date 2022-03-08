package dev.yjyoon.coverist.ui.cover.generation

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yjyoon.coverist.exception.NonexistentTagException
import dev.yjyoon.coverist.exception.TagAlreadyExistsException
import dev.yjyoon.coverist.data.remote.model.Book
import dev.yjyoon.coverist.data.remote.model.Cover
import dev.yjyoon.coverist.data.remote.model.Genre
import dev.yjyoon.coverist.repository.CoverRepository
import dev.yjyoon.coverist.repository.GenreRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenerateCoverViewModel @Inject constructor(
    private val genreRepository: GenreRepository,
    private val coverRepository: CoverRepository
) : ViewModel() {

    var bookTitle by mutableStateOf("")
    var bookAuthor by mutableStateOf("")
    var bookGenre by mutableStateOf<Genre?>(null)
    var bookSubGenre by mutableStateOf<Genre?>(null)
    var bookTags = mutableStateListOf<String>()
    var bookPublisher by mutableStateOf<Uri?>(null)
    var isBookPublisherEmpty by mutableStateOf(false)

    var isGenerating by mutableStateOf(false)

    fun editTitle(title: String) {
        bookTitle = title.trim()
    }

    fun editAuthor(author: String) {
        bookAuthor = author.trim()
    }

    fun editGenre(genre: Genre) {
        bookGenre = genre
        bookSubGenre = null
    }

    fun editSubGenre(subGenre: Genre) {
        bookSubGenre = subGenre
    }

    fun addTag(tag: String) {
        if (bookTags.contains(tag.trim())) throw TagAlreadyExistsException()
        bookTags.add(tag.trim())
    }

    fun deleteTag(tag: String) {
        if (!bookTags.contains(tag.trim())) throw NonexistentTagException()
        bookTags.remove(tag.trim())
    }

    fun isInvalidTag(tag: String): Boolean {
        if(tag.trim().isEmpty()) return true
        if(bookTags.contains(tag.trim())) return true
        return false
    }

    fun isNotFullTags(): Boolean {
        return bookTags.size < 10
    }

    fun loadGenres(): LiveData<List<Genre>> {
        lateinit var genres: LiveData<List<Genre>>

        viewModelScope.launch {
            genres = genreRepository.getGenres()
        }
        return genres!!
    }

    fun loadSubGenres(): LiveData<List<Genre>> {
        lateinit var subGenres: LiveData<List<Genre>>

        viewModelScope.launch {
            subGenres = genreRepository.getSubGenres(bookGenre!!.id)
        }
        return subGenres
    }

    fun editPublisher(uri: Uri?) {
        bookPublisher = uri
    }

    fun deletePublisher() {
        bookPublisher = null
    }

    fun setPublisherEmpty(empty: Boolean) {
        isBookPublisherEmpty = empty
    }

    fun isValidInput(step: Int): Boolean =
        when (step) {
            0 -> bookTitle != "" && bookAuthor != ""
            1 -> bookGenre != null
            2 -> bookSubGenre != null
            3 -> bookTags.isNotEmpty()
            4 -> bookPublisher != null || isBookPublisherEmpty
            else -> false
        }

    fun generateCover(): LiveData<List<Cover>> {
        lateinit var covers: LiveData<List<Cover>>

        val book = Book(
            title = bookTitle,
            author = bookAuthor,
            genre = bookGenre!!.text,
            subGenre = bookSubGenre!!.text,
            tags = bookTags,
            publisher = bookPublisher?.toString()
        )

        viewModelScope.launch {
            covers = coverRepository.generateCover(book)
        }

        return covers
    }
}