package com.gamovation.feature.store

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.gamovation.core.data.billing.BillingDataSource
import com.gamovation.core.data.billing.BillingProductType
import com.gamovation.core.data.repository.OfflineUserInfoPreferencesRepository
import com.gamovation.core.domain.billing.UserVipType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class StoreScreenViewModel @Inject constructor(
    private val billingDataSource: BillingDataSource,
    private val userInfoPreferencesRepository: OfflineUserInfoPreferencesRepository
) : ViewModel() {
    fun getInAppProductsDetails() = billingDataSource.productsDetailsFlow
    fun purchaseProduct(
        details: ProductDetails,
        type: BillingProductType,
        onError: () -> Unit,
        onRequestActivity: () -> ComponentActivity
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            val currentType = userInfoPreferencesRepository.getUserVipType().first()
            when (type) {
                BillingProductType.REMOVE_ADS, BillingProductType.VIP -> {
                    if (currentType != UserVipType.BASE) {
                        onError()
                    } else {
                        billingDataSource.purchaseProduct(details, type, onRequestActivity)
                    }
                }

                else -> billingDataSource.purchaseProduct(details, type, onRequestActivity)
            }
        }

    fun watchAdReward() = viewModelScope.launch(Dispatchers.IO) {
        userInfoPreferencesRepository.buyCurrency(25)
    }
}
