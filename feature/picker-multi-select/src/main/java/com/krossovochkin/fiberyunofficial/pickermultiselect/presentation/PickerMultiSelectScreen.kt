/*
   Copyright 2020 Vasya Drobushkov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.krossovochkin.fiberyunofficial.pickermultiselect.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.krossovochkin.fiberyunofficial.domain.FieldData

@Composable
fun PickerMultiSelectScreen(
    item: FieldData.MultiSelectFieldData,
    onConfirm: (addedItems: List<FieldData.EnumItemData>, removedItems: List<FieldData.EnumItemData>) -> Unit,
    onDismiss: () -> Unit,
) {
    val selectedItemsIds = item.selectedValues.map { it.id }.toSet()
    val checkedStates = remember {
        mutableStateOf(item.values.map { it.id in selectedItemsIds }.toList())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(item.title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                item.values.forEachIndexed { index, enumItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkedStates.value[index],
                            onCheckedChange = { isChecked ->
                                val updatedList = checkedStates.value.toMutableList()
                                updatedList[index] = isChecked
                                checkedStates.value = updatedList.toList()
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(enumItem.title)
                    }
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(android.R.string.cancel.toString())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedItems = item.values.filterIndexed { index, _ ->
                        checkedStates.value[index]
                    }
                    val addedItems = selectedItems.filter { value -> value !in item.selectedValues }
                    val removedItems = item.selectedValues.filter { value -> value !in selectedItems }
                    onConfirm(addedItems, removedItems)
                }
            ) {
                Text(android.R.string.ok.toString())
            }
        }
    )
}
