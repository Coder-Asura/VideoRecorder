/*
 *
 *   Created by Sina Dalvand on 1/21/2020
 *   Copyright (c) 2020 . All rights reserved.
 *
 *
 */

package ir.sinadalvand.videorecoreder.di.keys

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass


@MustBeDocumented
@MapKey

annotation class ViewModelKey(val mclass:KClass<out ViewModel>)