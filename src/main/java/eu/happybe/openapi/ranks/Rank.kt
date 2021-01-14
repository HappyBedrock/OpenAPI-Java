package eu.happybe.openapi.ranks

class Rank @JvmOverloads constructor(@field:Getter private val rankName: String, @field:Getter private val formatting: String, @field:Getter private val permissions: Array<String>, @field:Getter private val isVisible: Boolean = true) {
    val formatForChat: String
        get() = if (isVisible) this.getFormatting() + this.getRankName().toUpperCase() + "§r " else ""
    val formatForDisplay: String
        get() = this.getRankName()
    val formatForNameTag: String
        get() = if (isVisible) this.getFormatting() + this.getRankName().toUpperCase() + "§r " else ""
}