package dev.olshevski.udu.ui.common

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import dev.olshevski.udu.R
import dev.olshevski.udu.network.model.Definition
import dev.olshevski.udu.network.model.Direction
import kotlinx.android.synthetic.main.item_definition.view.*
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class DefinitionViewHolder(
    parent: ViewGroup,
    private val listener: DefinitionViewListener
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_definition, parent, false)
) {

    private val context: Context = parent.context

    companion object {
        private val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(Locale.US)
            .withZone(ZoneOffset.UTC)
    }

    fun bind(definition: Definition) {
        itemView.word.apply {
            text = definition.word
            setOnClickListener {
                listener.onLinkClicked(definition.word)
            }
        }
        itemView.author.text =
            context.getString(R.string.item_definition__by_whom, definition.author)
        itemView.writtenOn.text = formatter.format(definition.writtenOn)
        itemView.date.apply {
            if (definition.date == null) {
                isVisible = false
            } else {
                isVisible = true
                text = definition.date
            }
        }
        itemView.definition.setTextWithClickableLinks(definition.definition)
        itemView.example.setTextWithClickableLinks(definition.example)
        itemView.thumb_up.apply {
            text = definition.thumbsUp.toString()
            isActivated = definition.currentVote == Direction.UP
            setOnClickListener {
                listener.onThumbClicked(definition, Direction.UP)
            }
        }
        itemView.thumb_down.apply {
            text = definition.thumbsDown.toString()
            isActivated = definition.currentVote == Direction.DOWN
            setOnClickListener {
                listener.onThumbClicked(definition, Direction.DOWN)
            }
        }
        itemView.share.setOnClickListener {
            listener.onShareClicked(definition)
        }
    }

    private fun TextView.setTextWithClickableLinks(text: String) {
        this.text = text.withClickableLinks(listener::onLinkClicked)
        this.movementMethod = LinkMovementMethod.getInstance()
    }

}