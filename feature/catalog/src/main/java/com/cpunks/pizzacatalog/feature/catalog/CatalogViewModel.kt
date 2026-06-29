package com.cpunks.pizzacatalog.feature.catalog

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import coil3.size.Size
import com.cpunks.pizzacatalog.domain.model.Pizza
import com.cpunks.pizzacatalog.domain.repository.PizzaRepository
import com.cpunks.pizzacatalog.domain.usecase.GetPizzasUseCase
import com.webtest.ads.frequency.AdFrequencyManager
import com.webtest.ads.interstitial.InterstitialAdManager
import com.webtest.ads.rewarded.RewardedAdManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CatalogUiState(
    val pizzas: List<Pizza> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val imagesReady: Boolean = false,
    val selectedSizes: Map<String, String> = emptyMap(),
    val quantities: Map<String, Int> = emptyMap(),
    val rewardAnimation: Boolean = false
)

@HiltViewModel
class CatalogViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getPizzas: GetPizzasUseCase,
    private val repository: PizzaRepository,
    private val interstitialAdManager: InterstitialAdManager,
    private val adFrequencyManager: AdFrequencyManager,
    private val rewardedAdManager: RewardedAdManager
) : ViewModel() {

    private var prefetchStarted = false

    private companion object {
        const val MAX_ATTEMPTS = 3
        const val RETRY_BASE_DELAY_MS = 1500L
    }

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    fun shouldShowInterstitial(): Boolean {
        return adFrequencyManager.shouldShow() &&
                interstitialAdManager.canShow()
    }

    private fun addPizzaToCart(pizzaId: String) {
        _uiState.update { state ->
            val current = state.quantities[pizzaId] ?: 1
            state.copy(
                quantities = state.quantities + (pizzaId to (current + 1))
            )
        }
    }

    fun watchRewarded(
        activity: Activity,
        pizzaId: String
    ) {
        val shown = rewardedAdManager.show(
            activity = activity,
            onReward = { addPizzaToCart(pizzaId) },
            onDismissed = { _uiState.update { it.copy(rewardAnimation = true) } }
        )

        if (!shown) {
            rewardedAdManager.load(context)
        }
    }

    fun rewardAnimationFinished() {
        _uiState.update {
            it.copy(rewardAnimation = false)
        }
    }

    init {
        observePizzas()
        refresh()
        interstitialAdManager.load(context)
        rewardedAdManager.load(context)
    }

    fun showInterstitial(activity: Activity) {
        interstitialAdManager.show(activity)
    }

    private fun observePizzas() {
        getPizzas()
            .onEach { pizzas ->
                _uiState.update { state ->
                    val newSizes = pizzas.associate { p ->
                        p.id to (state.selectedSizes[p.id] ?: p.defaultSize)
                    }
                    val newQty = pizzas.associate { p ->
                        p.id to (state.quantities[p.id] ?: 1)
                    }
                    state.copy(
                        pizzas = pizzas,
                        isLoading = false,
                        selectedSizes = newSizes,
                        quantities = newQty
                    )
                }

                if (pizzas.isNotEmpty() && !prefetchStarted) {
                    prefetchStarted = true
                    prefetchImages(pizzas)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun prefetchImages(pizzas: List<Pizza>) {
        viewModelScope.launch {
            val loader = SingletonImageLoader.get(context)
            pizzas.map { pizza ->
                async {
                    val request = ImageRequest.Builder(context)
                        .data(pizza.imageUrl)
                        .size(Size.ORIGINAL)
                        .build()
                    loader.execute(request)
                }
            }.awaitAll()
            _uiState.update { it.copy(imagesReady = true) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            repeat(MAX_ATTEMPTS) { attempt ->
                val result = runCatching { repository.refreshPizzas() }
                if (result.isSuccess) {
                    _uiState.update { it.copy(error = null) }
                    return@launch
                }
                if (attempt < MAX_ATTEMPTS - 1) {
                    delay(RETRY_BASE_DELAY_MS * (attempt + 1))
                }
            }

            _uiState.update { state ->
                if (state.pizzas.isEmpty()) {
                    state.copy(
                        isLoading = false,
                        error = "No internet connection",
                        imagesReady = true
                    )
                } else {
                    state
                }
            }
        }
    }

    fun retry() {
        _uiState.update { it.copy(isLoading = true, error = null, imagesReady = false) }
        refresh()
    }

    fun selectSize(pizzaId: String, size: String) {
        _uiState.update { it.copy(selectedSizes = it.selectedSizes + (pizzaId to size)) }
    }

    fun increment(pizzaId: String) {
        _uiState.update { state ->
            val q = (state.quantities[pizzaId] ?: 1) + 1
            state.copy(quantities = state.quantities + (pizzaId to q))
        }
    }

    fun decrement(pizzaId: String) {
        _uiState.update { state ->
            val q = (state.quantities[pizzaId] ?: 1).coerceAtLeast(2) - 1
            state.copy(quantities = state.quantities + (pizzaId to q))
        }
    }
}
