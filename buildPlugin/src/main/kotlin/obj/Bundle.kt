package obj

import org.jetbrains.kotlin.com.google.gson.annotations.SerializedName

data class Bundle(
    @SerializedName("alert_url") val alertUrl: String?,
    @SerializedName("appointment_url") val appointmentUrl: String?,
    @SerializedName("cme_url") val cmeUrl: String?,
    @SerializedName("emc_url") val emcUrl: String?,
    @SerializedName("file_url") val fileUrl: String?,
    @SerializedName("health_summary_url") val healthSummaryUrl: String?,
    @SerializedName("iam_url") val iamUrl: String?,
    @SerializedName("invoice_url") val invoiceUrl: String?,
    @SerializedName("lab_url") val labUrl: String?,
    @SerializedName("prefix") val prefix: String?,
    @SerializedName("prescription_url") val prescriptionUrl: String?,
    @SerializedName("marketplace_url") val marketplaceUrl: String?,
    @SerializedName("search_url") val searchUrl: String?,
    @SerializedName("teleconsult_url") val teleConsultUrl: String?,
    @SerializedName("fw_url") val sgpfwUrl: String?,
    @SerializedName("manapay_url") val manapayUrl: String?,
    @SerializedName("report_url") val reportUrl: String?,
    @SerializedName("manasearch_url") val manaSearchUrl: String?,
    @SerializedName("homepage_url") val homePageUrl: String?,
    @SerializedName("promotion_url") val promotionUrl: String?,
    @SerializedName("forum_url") val forumUrl: String?,
    @SerializedName("manacare_url") val manacareUrl: String?,
    @SerializedName("manasocial_url") val manaSocialUrl: String?,
    @SerializedName("manasocial_web_url") val manaSocialWebUrl: String?,
)