/*
 * Copyright 2016 drakeet. https://github.com/drakeet
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

package me.drakeet.multitype;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.List;

/**
 * @author drakeet
 */
public class MultiTypeAdapter extends RecyclerView.Adapter<ViewHolder> implements TypePool {

    @Nullable private List<?> items;
    @NonNull private TypePool delegate;
    @Nullable protected LayoutInflater inflater;
    @Nullable private FlatTypeAdapter providedFlatTypeAdapter;


    public MultiTypeAdapter() {
        this(null);
    }


    public MultiTypeAdapter(@Nullable List<?> items) {
        this(items, new MultiTypePool(), null);
    }


    public MultiTypeAdapter(@Nullable List<?> items, int initialCapacity) {
        this(items, new MultiTypePool(initialCapacity), /* providedFlatTypeAdapter: */ null);
    }


    public MultiTypeAdapter(@Nullable List<?> items, @NonNull TypePool pool) {
        this(items, pool, /* providedFlatTypeAdapter: */ null);
    }


    public MultiTypeAdapter(
        @Nullable List<?> items, @NonNull TypePool delegate,
        @Nullable FlatTypeAdapter providedFlatTypeAdapter) {
        this.items = items;
        this.delegate = delegate;
        this.providedFlatTypeAdapter = providedFlatTypeAdapter;
    }


    @Override
    public void register(@NonNull Class<?> clazz, @NonNull ItemViewBinder binder) {
        delegate.register(clazz, binder);
    }


    public final void registerAll(@NonNull final TypePool pool) {
        for (int i = 0; i < pool.getContents().size(); i++) {
            delegate.register(pool.getContents().get(i), pool.getItemViewBinders().get(i));
        }
    }


    /**
     * Update the items atomically and safely.
     * It is recommended to use this method to update the data.
     * <p>e.g. {@code adapter.setItems(new Items(changedItems));}</p>
     *
     * <p>Note: If you want to refresh the list views, you should
     * call {@link RecyclerView.Adapter#notifyDataSetChanged()} by yourself.</p>
     *
     * @param items The <b>new</b> items list.
     * @since v2.4.1
     */
    public void setItems(@Nullable List<?> items) {
        this.items = items;
    }


    /**
     * Set the TypePool to hold the types and view binders.
     *
     * @param typePool The TypePool implementation
     */
    public void setTypePool(@NonNull TypePool typePool) {
        this.delegate = typePool;
    }


    /**
     * Set the FlatTypeAdapter to instead of the default inner FlatTypeAdapter of
     * MultiTypeAdapter.
     * <p>Note: You could use {@link FlatTypeClassAdapter} and {@link FlatTypeItemAdapter}
     * to create a special FlatTypeAdapter conveniently.</p>
     *
     * @param flatTypeAdapter the FlatTypeAdapter
     * @since v2.3.2
     */
    public void setFlatTypeAdapter(@NonNull FlatTypeAdapter flatTypeAdapter) {
        this.providedFlatTypeAdapter = flatTypeAdapter;
    }


    @Override
    public final int getItemViewType(int position) {
        assert items != null;
        Object item = items.get(position);
        return indexOf(flattenClass(item));
    }


    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int indexViewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        ItemViewBinder binder = getBinderByIndex(indexViewType);
        binder.adapter = MultiTypeAdapter.this;
        assert inflater != null;
        return binder.onCreateViewHolder(inflater, parent);
    }


    @Override
    public final void onBindViewHolder(ViewHolder holder, int position) {}


    @Override @SuppressWarnings("unchecked")
    public final void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        assert items != null;
        Object item = items.get(position);
        ItemViewBinder binder = getBinderByClass(flattenClass(item));
        binder.onBindViewHolder(holder, flattenItem(item), payloads);
    }


    @Override
    public final int getItemCount() {
        return items == null ? 0 : items.size();
    }


    @Override
    public int indexOf(@NonNull Class<?> clazz) throws BinderNotFoundException {
        int index = delegate.indexOf(clazz);
        if (index >= 0) {
            return index;
        }
        throw new BinderNotFoundException(clazz);
    }


    @NonNull
    final Class<?> flattenClass(@NonNull final Object item) {
        if (providedFlatTypeAdapter != null) {
            return providedFlatTypeAdapter.onFlattenClass(item);
        }
        return item.getClass();
    }


    @NonNull
    private Object flattenItem(@NonNull final Object item) {
        if (providedFlatTypeAdapter != null) {
            return providedFlatTypeAdapter.onFlattenItem(item);
        }
        return item;
    }


    @NonNull @Override
    public List<Class<?>> getContents() {
        return delegate.getContents();
    }


    @NonNull @Override
    public List<ItemViewBinder> getItemViewBinders() {
        return delegate.getItemViewBinders();
    }


    @NonNull @Override
    public ItemViewBinder getBinderByIndex(int index) {
        return delegate.getBinderByIndex(index);
    }


    @NonNull @Override
    public <T extends ItemViewBinder> T getBinderByClass(@NonNull Class<?> clazz) {
        return delegate.getBinderByClass(clazz);
    }


    @Nullable
    public List<?> getItems() { return items; }


    @NonNull
    public TypePool getTypePool() {
        return delegate;
    }
}
