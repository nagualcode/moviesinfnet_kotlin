package com.infnet.moviesinfnet.utils.ext

import androidx.lifecycle.MutableLiveData


fun MutableLiveData<String>.trim()
{
    value = value?.trim()
}