/*
 * Copyright (c) 2016-present. Drakeet Xu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.drakeet.multitype.sample.normal

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import me.drakeet.multitype.MultiTypeAdapter
import me.drakeet.multitype.sample.MenuBaseActivity
import me.drakeet.multitype.sample.R
import java.util.*

/**
 * @author Drakeet Xu
 */
class NormalActivity : MenuBaseActivity() {

  private lateinit var adapter: MultiTypeAdapter
  private lateinit var items: MutableList<Any>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_list)
    val recyclerView = findViewById<RecyclerView>(R.id.list)

    adapter = MultiTypeAdapter()
    adapter.register(TextItemViewBinder())
    adapter.register(ImageItemViewBinder())
    adapter.register(RichItemViewBinder())
    recyclerView.adapter = adapter

    val textItem = TextItem("world")
    val imageItem = ImageItem(R.mipmap.ic_launcher)
    val richItem = RichItem("小艾大人赛高", R.drawable.img_11)

    items = ArrayList()
    for (i in 0..19) {
      items.add(textItem)
      items.add(imageItem)
      items.add(richItem)
    }
    adapter.items = items
    adapter.notifyDataSetChanged()
  }
}
