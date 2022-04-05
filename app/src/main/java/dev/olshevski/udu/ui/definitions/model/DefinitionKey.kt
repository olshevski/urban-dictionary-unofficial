package dev.olshevski.udu.ui.definitions.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class DefinitionKey : Parcelable

@Parcelize
class DefIdKey(val defId: Long) : DefinitionKey()

@Parcelize
class TermKey(val term: String) : DefinitionKey()