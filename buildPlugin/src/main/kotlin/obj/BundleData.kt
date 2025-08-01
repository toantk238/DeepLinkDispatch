package obj

import org.jetbrains.kotlin.com.google.gson.annotations.SerializedName

data class BundleData(
    @SerializedName("data") val data: List<Bundle>
)