package by.krossovochkin.fiberyunofficial.entitydetails.presentation


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import by.krossovochkin.fiberyunofficial.core.domain.FiberyEntityData
import by.krossovochkin.fiberyunofficial.core.presentation.ListItem
import by.krossovochkin.fiberyunofficial.entitydetails.DaggerEntityDetailsComponent
import by.krossovochkin.fiberyunofficial.entitydetails.EntityDetailsParentComponent
import by.krossovochkin.fiberyunofficial.entitydetails.R
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.fragment_entity_details.*
import kotlinx.android.synthetic.main.item_field_header.*
import kotlinx.android.synthetic.main.item_field_relation.*
import kotlinx.android.synthetic.main.item_field_rich_text.*
import kotlinx.android.synthetic.main.item_field_text.*
import javax.inject.Inject

class EntityDetailsFragment(
    private val entityDetailsParentComponent: EntityDetailsParentComponent
) : Fragment(R.layout.fragment_entity_details) {

    @Inject
    lateinit var viewModel: EntityDetailsViewModel

    private val adapter = ListDelegationAdapter<List<ListItem>>(
        adapterDelegateLayoutContainer<FieldHeaderItem, ListItem>(
            layout = R.layout.item_field_header
        ) {
            bind {
                fieldHeaderPublicIdTextView.text = item.publicId
                fieldHeaderTitleTextView.text = item.title
            }
        },
        adapterDelegateLayoutContainer<FieldTextItem, ListItem>(
            layout = R.layout.item_field_text
        ) {
            bind {
                fieldTextTitleView.text = item.title
                fieldTextView.text = item.text
            }
        },
        adapterDelegateLayoutContainer<FieldRichTextItem, ListItem>(
            layout = R.layout.item_field_rich_text
        ) {
            bind {
                richTextTitleView.text = item.title

                Markwon.create(context).setMarkdown(richTextView, item.value)
            }
        },
        adapterDelegateLayoutContainer<FieldRelationItem, ListItem>(
            layout = R.layout.item_field_relation
        ) {
            bind {
                fieldRelationTitleView.text = item.title
            }
        }
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        DaggerEntityDetailsComponent.builder()
            .fragment(this)
            .entityDetailsParentComponent(entityDetailsParentComponent)
            .build()
            .inject(this)

        entityDetailsRecyclerView.layoutManager = LinearLayoutManager(context)
        entityDetailsRecyclerView.adapter = adapter

        viewModel.items.observe(viewLifecycleOwner, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })
    }

    interface ArgsProvider {

        fun getEntityDetailsArgs(arguments: Bundle): Args
    }

    data class Args(
        val entityData: FiberyEntityData
    )

}
