package com.infnet.moviesinfnet.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.infnet.moviesinfnet.R
import com.infnet.moviesinfnet.model.LocalMovie
import com.infnet.moviesinfnet.network.firebase.toLocalMovie
import com.infnet.moviesinfnet.utils.const.DatabaseFields
import com.infnet.moviesinfnet.utils.const.PASSWD_MIN_LENGTH
import com.infnet.moviesinfnet.utils.ext.makeEvent
import com.infnet.moviesinfnet.utils.helper.Event
import com.infnet.moviesinfnet.utils.helper.Message
import com.infnet.moviesinfnet.utils.helper.QueryListener
import com.infnet.moviesinfnet.utils.status.BaseStatus
import com.infnet.moviesinfnet.utils.status.EventMessageStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FirebaseRepository @Inject constructor()
{
    companion object
    {
        private val userMovieData = Firebase.database.getReference(DatabaseFields.USERS_MOVIE_DATA)

        private fun getUserAllMoviesDataDbRef(id: String) = userMovieData.child(id)

        private fun getUserMovieDbRef(userId: String, movieId: String) =
                getUserAllMoviesDataDbRef(userId).child(movieId)

        private fun getUserMovieIsWatchedDbRef(userId: String, movieId: String) =
                getUserAllMoviesDataDbRef(userId).child(movieId)
                    .child(DatabaseFields.MOVIE_IS_WATCHED_FIELD)
    }

    // region logged user

    private val auth = Firebase.auth

    private val _loggedUser = MutableStateFlow(auth.currentUser)
    val loggedUser = _loggedUser.asStateFlow()

    /**
     * probably this will newer throw nullPointerException in [com.infnet.moviesinfnet.ui.activities.MainActivity]
     * when [_loggedUser] becomes null, [com.infnet.moviesinfnet.ui.activities.MainActivity] should be closed
     */
    private val requireUser: FirebaseUser
        get() = _loggedUser.value!!

    init
    {
        auth.addAuthStateListener {
            Timber.d("Auth state changed to $it")
            _loggedUser.value = it.currentUser
        }
    }

    // endregion

    // region sign in/register

    @ExperimentalCoroutinesApi
    fun loginUser(email: String?, passwd: String?): Flow<EventMessageStatus> = channelFlow {
        send(EventMessageStatus.Loading)

        val e = email?.trim()
        val p = passwd?.trim()

        if (e.isNullOrBlank()) // empty email
        {
            send(EventMessageStatus.Failed(Event(Message(R.string.empty_email))))
            close()
        }
        else if (p.isNullOrBlank() || p.length < PASSWD_MIN_LENGTH) // too short passwd
        {
            send(
                EventMessageStatus.Failed(
                    Event(
                        Message(
                            R.string.too_short_passwd,
                            listOf(PASSWD_MIN_LENGTH)
                        )
                    )
                )
            )
            close()
        }
        else
        {
            auth.signInWithEmailAndPassword(e, p)
                .addOnSuccessListener {
                    launch {
                        send(EventMessageStatus.Success(Event(Message(R.string.logged_in_correctly))))
                        close()
                    }
                }
                .addOnFailureListener {
                    launch {
                        Timber.d("$it")
                        send(EventMessageStatus.Failed(Event(Message(R.string.wrong_email_or_passwd))))
                        close()
                    }
                }
            awaitClose()
        }
    }

    @ExperimentalCoroutinesApi
    fun registerUser(
        email: String?,
        passwd: String?,
        passwdConf: String?
    ): Flow<EventMessageStatus> = channelFlow {
        send(EventMessageStatus.Loading)

        val e = email?.trim()
        val p = passwd?.trim()
        val pc = passwdConf?.trim()

        if (e.isNullOrBlank()) // empty email
        {
            send(EventMessageStatus.Failed(Event(Message(R.string.empty_email))))
            close()
        }
        else if (p.isNullOrBlank() || p.length < PASSWD_MIN_LENGTH) // too short passwd
        {
            send(
                EventMessageStatus.Failed(
                    Event(
                        Message(
                            R.string.too_short_passwd,
                            listOf(PASSWD_MIN_LENGTH)
                        )
                    )
                )
            )
            close()
        }
        else if (p != pc) //passwords are different
        {
            send(EventMessageStatus.Failed(Event(Message(R.string.diff_passwd))))
            close()
        }
        else // user can be registered
        {
            auth.createUserWithEmailAndPassword(e, p)
                .addOnSuccessListener {
                    Timber.d("Successfully registered")
                    launch {
                        send(EventMessageStatus.Success(Event(Message(R.string.successfully_registered))))
                        close()
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.d("Registration failed")
                    launch {
                        send(
                            EventMessageStatus.Failed(
                                exception.makeEvent
                            )
                        )
                        close()
                    }
                }
        }

        awaitClose()
    }

    fun logOut() = auth.signOut()

    // endregion

    // region user movie data

    @ExperimentalCoroutinesApi
    fun addMovie(movie: LocalMovie, isWatched: Boolean): Flow<EventMessageStatus> = channelFlow {

        send(EventMessageStatus.Loading)

        val movieData = movie.apply {
            this.isWatched = isWatched
        }.dbHashMap()

        getUserAllMoviesDataDbRef(requireUser.uid).child(movie.id).setValue(movieData)
            .addOnSuccessListener {
                Timber.d("Movie [$movieData] saved successfully")
                launch {
                    send(EventMessageStatus.Success(Event(Message(if (isWatched) R.string.movie_added_to_watched else R.string.movie_added_towatch))))
                    close()
                }
            }
            .addOnFailureListener {
                Timber.d("Movie [$movieData] was NOT saved successfully")
                launch {
                    send(EventMessageStatus.Failed(it.makeEvent))
                    close()
                }
            }

        awaitClose()
    }

    var isWatchedListener: QueryListener? = null
        private set


    @ExperimentalCoroutinesApi
    fun getMovieIsWatched(movieId: String): Flow<BaseStatus<Boolean?>> = channelFlow {

        isWatchedListener?.removeListener()

        send(BaseStatus.Loading)

        val q = getUserMovieIsWatchedDbRef(requireUser.uid, movieId)
        val l = object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                Timber.d("Movie with id [$movieId] data retrieved")

                val isWatched = snapshot.getValue(Boolean::class.java)

                launch {
                    send(BaseStatus.Success(isWatched))
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

                Timber.d("Movie with id [$movieId] cancelled")
                launch {
                    send(BaseStatus.Failed(error.toException().makeEvent))
                }
            }
        }

        isWatchedListener = QueryListener(q, l)
        isWatchedListener?.addListener()

        awaitClose()
    }

    fun removeMovie(movieId: String)
    {
        getUserMovieDbRef(requireUser.uid, movieId).ref.removeValue()
    }


    var allMoviesListener: QueryListener? = null
        private set

    @ExperimentalCoroutinesApi
    fun getAllMovies(): Flow<BaseStatus<List<LocalMovie>>> = channelFlow {
        send(BaseStatus.Loading)
        allMoviesListener?.removeListener()

        val q = getUserAllMoviesDataDbRef(requireUser.uid)
        val l = object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                Timber.d("Get all movies request completed. $snapshot")
                val movies = snapshot.getValue(DatabaseFields.FirebaseMovieInstance)
                Timber.d(movies.toString())
                if (movies == null)
                {
                    launch {
                        send(BaseStatus.Success<List<LocalMovie>>(listOf()))
                    }
                }
                else
                {
                    launch {
                        send(
                            BaseStatus.Success(
                                movies.map {
                                    it.toLocalMovie()
                                }
                            )
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError)
            {
                Timber.d("Get all movies request cancelled")
                launch {
                    send(
                        BaseStatus.Failed(
                            error.toException().makeEvent
                        )
                    )
                }
            }
        }

        allMoviesListener = QueryListener(q, l)
        allMoviesListener?.addListener()

        awaitClose()
    }

    // endregion
}