/*
 * Copyright (C) 2018 Tang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tang.com.recurve.binding

import android.app.Activity
import android.app.Application
import android.databinding.DataBindingComponent
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import javax.inject.Inject

/**
 * Created by tang on 2018/3/2.
 * A Data Binding Component implementation for recurve.
 */
class RecurveDataBindingComponent(private val requestManager: RequestManager): DataBindingComponent {

    @DrawableRes private var placeholderRes: Int = 0
    @DrawableRes var fallbackRes: Int = 0
    @DrawableRes var errorRes: Int = 0

    override fun getImageBindingAdapters(): ImageBindingAdapters =
            ImageBindingAdapters(requestManager,placeholderRes,fallbackRes,errorRes)



}